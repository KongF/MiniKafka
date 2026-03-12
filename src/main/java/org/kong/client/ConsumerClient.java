package org.kong.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.kong.protocol.Request;

public class ConsumerClient {

    private final Channel channel;

    private final ObjectMapper mapper = new ObjectMapper();

    private long offset = 0;
    private String topic;
    private int partition;

    public ConsumerClient(String host, int port, String topic, int partition) throws Exception {
        this.topic = topic;
        this.partition = partition;
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer());

        channel = bootstrap.connect(host, port).sync().channel();

    }

    public void poll() throws Exception {

        Request request = new Request();
        request.setType("FETCH");
        request.setTopic(topic);
        request.setPartition(partition);
        request.setOffset(offset);

        String json = mapper.writeValueAsString(request);

        channel.writeAndFlush(json + "\n");
    }
    public void nextOffset(long offset){
        this.offset = offset;
    }

}
