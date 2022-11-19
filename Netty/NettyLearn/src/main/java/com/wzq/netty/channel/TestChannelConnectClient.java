package com.wzq.netty.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Client
 * 处理ChannelFuture的connect问题
 *
 * @author wzq
 * @create 2022-11-18 22:13
 */
@Slf4j
public class TestChannelConnectClient {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                }).connect("localhost", 7);

        // 方法一：使用sync方法同步阻塞，等待连接成功之后继续向下执行
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.debug("{}", channel);
        channel.writeAndFlush("你好！");

        // 方法二：使用addListener异步回调
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.debug("channel : {}", channel);
                channel.writeAndFlush("你好！");
            }
        });

    }

}
