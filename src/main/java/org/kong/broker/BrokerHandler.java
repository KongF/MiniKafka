package org.kong.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.kong.Topic;
import org.kong.TopicManager;
import org.kong.LogReader;
import org.kong.protocol.Request;
import org.kong.protocol.Response;
import org.kong.storage.Partition;

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

        byte[] data = partition.fetch(request.getOffset());

        if (data == null) {

            Response response = new Response(false, "no message");
            ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");

            return;
        }

        Response response = new Response(data, request.getOffset());

        ctx.writeAndFlush(mapper.writeValueAsString(response) + "\n");
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
}
