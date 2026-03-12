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
        }
    }

    private void handleFetch(ChannelHandlerContext ctx, Request request) throws Exception {

        Topic topic = BrokerContext.TOPIC_MANAGER.getTopic(request.getTopic());

        if (topic == null) {
            Response response = new Response(false, "topic not exist");
            ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
            return;
        }

        Partition partition = topic.getPartitions().get(request.getPartition());

        FetchResult result = partition.fetch(request.getOffset());

        if (result == null) {
            Response response = new Response(false, "no message");
            ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");

            return;
        }
        FileChannel fileChannel = result.getChannel();
        long position = result.getPosition();
        long length = result.getLength();

        // ===== Zero Copy 关键代码 =====
        ctx.writeAndFlush(
                new DefaultFileRegion(
                        fileChannel,
                        position,
                        length
                )
        );

    }

    private void handleSend(ChannelHandlerContext ctx, Request request) throws Exception {
        Topic topic = topicManager.getTopic(request.getTopic());
        if(topic == null){
            Response response = new Response(false,"Topic not found");
            ctx.writeAndFlush(mapper.writeValueAsString(response)+"\n");
            return;
        }

        int partitionIndex = Math.abs(request.getKey().hashCode()) % topic.getPartitions().size();

        Partition partition = topic.getPartitions().get(partitionIndex);

        long offset = partition.append(request.getBody());

        Response response = new Response(true,"offset: "+offset,request.getBody());
        ctx.writeAndFlush(mapper.writeValueAsString(response)+"\n");
    }
    private void handleJoinGroup(ChannelHandlerContext ctx, Request request) throws Exception {

        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.joinGroup(request.getGroupId(), request.getConsumerId());
        boolean leader = group.isLeader(request.getConsumerId());
        Response response = new Response();
        response.setSuccess(true);

        response.setLeader(leader);
        response.setMembers(group.getMembers());
        ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
    }

    private void handleSyncGroup(ChannelHandlerContext ctx, Request request) throws Exception {
        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.getGroup(request.getGroupId());
        Topic topic = BrokerContext.TOPIC_MANAGER.getTopic(request.getTopic());

        Map<String,List<Integer>> assignment = group.rebalance(topic.getPartitions().size());

        List<Integer> partitions = assignment.get(request.getConsumerId());
        
        Response response = new Response();
        response.setSuccess(true);
        response.setPartitions(partitions);
        ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
    }
    private void handleHeartbeat(ChannelHandlerContext ctx, Request request) {

        ConsumerGroup group = BrokerContext.GROUP_COORDINATOR.getGroup(request.getGroupId());

        if (group != null) {
            group.heartbeat(request.getConsumerId());
        }
    }
    private void handleCommit(ChannelHandlerContext ctx, Request request) {

        BrokerContext.OFFSET_MANAGER.commit(
                request.getGroupId(),
                request.getTopic(),
                request.getPartition(),
                request.getOffset()
        );
    }
}
