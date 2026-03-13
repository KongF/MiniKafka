package org.kong.broker;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.kong.protocol.RequestDecoder;
import org.kong.protocol.ResponseEncoder;

/**
 * Broker 通道初始化器，负责为每个客户端连接配置 Netty 管道
 */
public class BrokerInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * 初始化客户端通道的处理管道
     * @param socketChannel 客户端 Socket 通道
     * @throws Exception 初始化失败时抛出异常
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 配置解码器：基于行的帧解码器（最大 1024 字节）和字符串解码器
//        pipeline.addLast(new LineBasedFrameDecoder(1024));
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));
        pipeline.addLast(new RequestDecoder());

        pipeline.addLast(new ResponseEncoder());

        // 添加业务处理器
        pipeline.addLast(new BrokerHandler());
    }
}
