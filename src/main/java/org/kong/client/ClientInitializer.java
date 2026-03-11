package org.kong.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {

        ChannelPipeline pipeline = ch.pipeline();

        // 按换行符拆包
        pipeline.addLast(new LineBasedFrameDecoder(1024));

        // String 解码
        pipeline.addLast(new StringDecoder());

        // String 编码
        pipeline.addLast(new StringEncoder());

        // 客户端响应处理
        pipeline.addLast(new ClientHandler());

    }

}
