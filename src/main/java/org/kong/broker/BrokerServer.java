package org.kong.broker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Broker 服务器，基于 Netty 实现消息队列服务端
 */
public class BrokerServer {
    /** Broker 监听端口号 */
    private final int port;

    /**
     * 构造 Broker 服务器实例
     * @param port 服务器监听端口
     */
    public BrokerServer(int port) {
        this.port = port;
    }

    /**
     * 启动 Broker 服务器
     * @throws Exception 启动失败时抛出异常
     */
    public void start() throws Exception {
        // 创建 boss 和 worker 线程组，boss 处理连接，worker 处理 IO
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 配置 Netty 服务器引导类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(io.netty.channel.socket.nio.NioServerSocketChannel.class)
                    .childHandler(new BrokerInitializer());
            // 绑定端口并等待关闭
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("MiniKafka Broker started at port " + port);
            future.channel().closeFuture().sync();
        }finally {
            // 优雅关闭线程组，释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
