package org.kong.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.kong.protocol.RequestEncoder;
import org.kong.protocol.ResponseDecoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {

        ChannelPipeline pipeline = ch.pipeline();

        // 按换行符拆包
        pipeline.addLast(new LineBasedFrameDecoder(1024));

        pipeline.addLast(new RequestEncoder());

        pipeline.addLast(new ResponseDecoder());


        // 客户端响应处理
        pipeline.addLast(new ClientHandler());

    }

}
