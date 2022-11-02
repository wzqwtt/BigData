package com.wzq.ch2.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author wzq
 * @create 2022-11-02 21:35
 */
public class EchoServer {

    public static int DEFAULT_PORT = 7;

    public static void main(String[] args) {
        int port;

        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
        }

        // 多线程事件循环气
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();      // boss
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();    // worker

        try {
            // 启动NIO服务的引导程序类
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)  // 设置EventLoopGroup
                    .channel(NioServerSocketChannel.class)  // 指明新的channel类型
                    .childHandler(new EchoServerHandler())  // 指定ChannelHandler
                    .option(ChannelOption.SO_BACKLOG, 128)  // 设置ServerChannel的一些选项
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //设置ServerChannel的子Channel选项

            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync();

            System.out.println("Echo Server 已启动，端口：" + port);

            // 等待服务器socket关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
