package com.wzq.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 客户端一直接收用户输入，然后发送给客户端；如果客户端输入'q或quit'，客户端自动关闭。
 *
 * @author wzq
 * @create 2022-11-18 22:56
 */
@Slf4j
public class TestChannelCloseClient {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 7);

        Channel channel = channelFuture.sync().channel();

        // 新启一个线程接收用户输入
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line) || "quit".equals(line)) {
                    channel.close();    // 并没有立刻关闭
                    break;
                }
                // 给服务端发送数据
                channel.writeAndFlush(line);
            }
        }).start();

//        // 方法一：sync同步阻塞
//        ChannelFuture closeFuture = channel.closeFuture();  // 获取响应关闭的ChannelFuture对象
//        closeFuture.sync();
//        log.debug("处理关闭之后的操作...");
//        group.shutdownGracefully(); // 优雅的关闭EventLoopGroup

        // 方法二：addListener异步回调
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                log.debug("处理关闭之后的操作...");
                group.shutdownGracefully();
            }
        });
    }

}
