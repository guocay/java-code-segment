package com.github.guocay.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

/**
 * Netty 服务器
 * @author GuoCay
 * @since 2023/3/2
 */
public class NettyServer {

    private static final Logger LOGGER = Logger.getLogger(NettyServer.class.getName());

    /**
     * 线程工厂
     */
    private static final ThreadFactory THREAD_FACTORY = FastThreadLocalThread::new;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup mainGroup = new KQueueEventLoopGroup(1, THREAD_FACTORY);
        EventLoopGroup workerGroup = new KQueueEventLoopGroup(10, THREAD_FACTORY);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup, workerGroup)
                    .channel(KQueueServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new Handler1())
                                    .addLast(new Handler2());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(8899)
                    .addListener(future -> LOGGER.info("Netty server starting..."));
            channelFuture.channel().closeFuture().sync();
        }finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
