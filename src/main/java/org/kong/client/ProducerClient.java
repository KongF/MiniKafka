package org.kong.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.kong.protocol.OldRequest;


public class ProducerClient {

    private final Channel channel;

    private final ObjectMapper mapper = new ObjectMapper();

    public ProducerClient(String host, int port) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer());

        channel = (Channel) bootstrap.connect(host, port).sync().channel();

    }

    public void send(String topic, String key, String msg)
            throws Exception {

        OldRequest request = new OldRequest();

        request.setType("SEND");

        request.setTopic(topic);

        request.setKey(key);

        request.setBody(msg.getBytes());

        channel.writeAndFlush(mapper.writeValueAsString(request) + "\n");

    }

}
