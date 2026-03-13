package org.kong.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import org.kong.Topic;
import org.kong.TopicManager;
import org.kong.broker.group.ConsumerGroup;
import org.kong.context.BrokerContext;
import org.kong.protocol.Request;
import org.kong.protocol.Response;
import org.kong.storage.FetchResult;
import org.kong.storage.Partition;

import java.nio.channels.FileChannel;

import java.util.List;
import java.util.Map;

public class BrokerHandler extends SimpleChannelInboundHandler<String> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TopicManager topicManager = BrokerContext.TOPIC_MANAGER;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Request request = mapper.readValue(msg, Request.class);

        if("SEND".equals(request.getType())){
            handleSend(ctx,request);
        }else if ("FETCH".equals(request.getType())) {
            handleFetch(ctx, request);
        } else if ("JOIN_GROUP".equals(request.getType())) {
            handleJoinGroup(ctx, request);
        }else if("SYNC_GROUP".equals(request.getType())){
            handleSyncGroup(ctx, request);
        } else if ("HEARTBEAT".equals(request.getType())) {
            handleHeartbeat(ctx, request);
        }else if ("OFFSET_COMMIT".equals(request.getType())) {
            handleCommit(ctx, request);
        }
    }

    /**
     * 处理客户端的消息拉取请求
     * 从指定的 Topic 和 Partition 中拉取消息，并通过 Zero Copy 技术将消息数据直接传输给客户端
     * 
     * @param ctx Netty 通道上下文，用于与客户端进行通信
     * @param request 客户端发送的拉取请求，包含以下信息：
     *                - topic: 要拉取的主题名称
     *                - partition: 分区索引
     *                - offset: 开始拉取的消息偏移量
     * @throws Exception 当处理过程中发生错误时抛出异常
     */
    private void handleFetch(ChannelHandlerContext ctx, Request request) throws Exception {

        // 获取请求的 Topic 对象
        Topic topic = BrokerContext.TOPIC_MANAGER.getTopic(request.getTopic());

        // 检查 Topic 是否存在，不存在则返回错误响应
        if (topic == null) {
            Response response = new Response(false, "topic not exist");
            ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
            return;
        }

        // 获取指定分区的 Partition 对象
        Partition partition = topic.getPartitions().get(request.getPartition());

        // 从分区中拉取指定偏移量的消息数据
        FetchResult result = partition.fetch(request.getOffset());

        // 检查是否成功获取到消息，未获取到则返回无消息响应
        if (result == null) {
            Response response = new Response(false, "no message");
            ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");

            return;
        }
        FileChannel fileChannel = result.getChannel();
        long position = result.getPosition();
        long length = result.getLength();

        // 使用 Zero Copy 技术将消息数据直接从文件通道传输到网络通道，避免用户态和内核态之间的数据拷贝
        ctx.writeAndFlush(
                new DefaultFileRegion(
                        fileChannel,
                        position,
                        length
                )
        );

    }

    /**
     * 处理客户端的消息发送请求
     * 将消息发送到指定的 Topic，并根据消息的 Key 计算哈希值分配到对应的 Partition
     * 
     * @param ctx Netty 通道上下文，用于与客户端进行通信
     * @param request 客户端发送的消息请求，包含以下信息：
     *                - topic: 要发送到的主题名称
     *                - key: 消息的键，用于计算分区索引
     *                - body: 消息体内容
     * @throws Exception 当处理过程中发生错误时抛出异常
     */
    private void handleSend(ChannelHandlerContext ctx, Request request) throws Exception {
        // 获取请求的 Topic 对象
        Topic topic = topicManager.getTopic(request.getTopic());
        // 检查 Topic 是否存在，不存在则返回错误响应
        if(topic == null){
            Response response = new Response(false,"Topic not found");
            ctx.writeAndFlush(mapper.writeValueAsString(response)+"\n");
            return;
        }

        // 根据消息 Key 的哈希值计算分区索引，确保相同 Key 的消息总是发送到同一分区
        int partitionIndex = Math.abs(request.getKey().hashCode()) % topic.getPartitions().size();

        // 获取目标分区的 Partition 对象
        Partition partition = topic.getPartitions().get(partitionIndex);

        // 将消息追加到分区中，并返回消息的偏移量
        long offset = partition.append(request.getBody());

        // 构建成功响应，包含消息偏移量和消息体
        Response response = new Response(true,"offset: "+offset,request.getBody());
        ctx.writeAndFlush(mapper.writeValueAsString(response)+"\n");
    }
    /**
     * 处理消费者加入消费组的请求
     * 将消费者添加到指定的消费组，并返回该消费者是否为 Leader 以及消费组的所有成员信息
     * 
     * @param ctx Netty 通道上下文，用于与客户端进行通信
     * @param request 客户端发送的加入组请求，包含以下信息：
     *                - groupId: 要加入的消费组 ID
     *                - consumerId: 消费者的唯一标识
     * @throws Exception 当处理过程中发生错误时抛出异常
     */
    private void handleJoinGroup(ChannelHandlerContext ctx, Request request) throws Exception {
        // 调用组协调器将消费者加入指定消费组
        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.joinGroup(request.getGroupId(), request.getConsumerId());
        // 判断当前消费者是否为消费组的 Leader
        boolean leader = group.isLeader(request.getConsumerId());
        Response response = new Response();
        response.setSuccess(true);

        // 设置响应信息，包括 Leader 标识和所有成员列表
        response.setLeader(leader);
        response.setGenerationId(group.generationId());
        response.setMembers(group.getMembers());
        ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
    }

    /**
     * 处理消费组的同步请求
     * 执行消费组的再平衡操作，为每个消费者分配分区，并返回当前消费者分配的分区列表
     * 
     * @param ctx Netty 通道上下文，用于与客户端进行通信
     * @param request 客户端发送的同步组请求，包含以下信息：
     *                - groupId: 消费组 ID
     *                - consumerId: 消费者的唯一标识
     *                - topic: 要消费的 Topic 名称
     * @throws Exception 当处理过程中发生错误时抛出异常
     */
    private void handleSyncGroup(ChannelHandlerContext ctx, Request request) throws Exception {
        // 获取指定的消费组对象
        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.getGroup(request.getGroupId());
        if(request.getGenerationId() != group.generationId()){
            Response error = new Response(false,"generationId mismatch");
            ctx.writeAndFlush(mapper.writeValueAsString(error)+"\n");
            return;
        }
        if(request.getAssignment() != null){
            group.completeRebalance(request.getAssignment());
        }

        // 获取当前消费者分配到的分区列表
        List<Integer> partitions = group.partitions(request.getConsumerId());
        
        Response response = new Response();
        response.setSuccess(true);
        // 设置响应信息，包含分配给当前消费者的分区列表
        response.setPartitions(partitions);
        ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
    }
    /**
     * 处理消费者的保活心跳请求
     * 更新消费者在消费组中的最后活跃时间，防止消费者被判定为失效
     * 
     * @param ctx Netty 通道上下文，用于与客户端进行通信
     * @param request 客户端发送的心跳请求，包含以下信息：
     *                - groupId: 消费组 ID
     *                - consumerId: 消费者的唯一标识
     */
    private void handleHeartbeat(ChannelHandlerContext ctx, Request request) {

        // 获取消费组对象
        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.getGroup(request.getGroupId());

        // 如果消费组存在，则更新消费者的最后活跃时间
        if (group != null) {
            group.heartbeat(request.getConsumerId());
        }
    }
    /**
     * 处理消费者的偏移量提交请求
     * 将消费者的消费进度（偏移量）持久化保存，以便故障恢复后从正确的位点继续消费
     * 
     * @param ctx Netty 通道上下文，用于与客户端进行通信
     * @param request 客户端发送的提交偏移量请求，包含以下信息：
     *                - groupId: 消费组 ID
     *                - topic: Topic 名称
     *                - partition: 分区索引
     *                - offset: 要提交的消费偏移量
     */
    private void handleCommit(ChannelHandlerContext ctx, Request request) {
        // 获取消费组对象
        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.getGroup(request.getGroupId());
        if (request.getGenerationId() != group.generationId()) {
            throw new RuntimeException("Illegal generation");
        }
        // 调用偏移量管理器提交消费进度
        BrokerContext.OFFSET_MANAGER.commit(
                request.getGroupId(),
                request.getTopic(),
                request.getPartition(),
                request.getOffset()
        );
    }
}
