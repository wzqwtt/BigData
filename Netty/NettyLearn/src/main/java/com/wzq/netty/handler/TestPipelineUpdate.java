package com.wzq.netty.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试PipeLine中Handler的增加、删除操作
 *
 * @author wzq
 * @create 2022-11-19 17:13
 */
@Slf4j
public class TestPipelineUpdate {

    public static void main(String[] args) {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MixInAndOutChannelInitializer())
                    .bind(7);

        } catch (Exception e) {
            e.printStackTrace();
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    // 入站处理器添加顺序
    static class InboundChannelInitializer extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            // 获取pipeline对象
            ChannelPipeline pipeline = ch.pipeline();

            // 在第一个位置添加StringDecoder
            pipeline.addFirst("StringDecoder", new StringDecoder());
            // 在pipeline末尾添加handler2
            pipeline.addLast("handler2", new InboundHandlerDemo());
            // 在handler2之前添加handler1
            pipeline.addBefore("handler2", "handler1", new InboundHandlerDemo());
            // 在handle2之后添加handler3
            pipeline.addAfter("handler2", "handler3", new InboundHandlerDemo());

            // 删除一个handler
            pipeline.remove("handler3");
            // 替换一个handler
            pipeline.replace("handler1", "handlerFirst", new InboundHandlerDemo());
        }
    }

    // 出站处理器添加顺序
    static class OutboundChannelInitializer extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            // 获取pipeline对象
            ChannelPipeline pipeline = ch.pipeline();

            // 在第一个位置添加StringDecoder
            pipeline.addFirst("StringDecoder", new StringDecoder());
            // 在pipeline末尾添加handler2
            pipeline.addLast("handler2", new OutboundHandlerDemo());
            // 在handler2之前添加handler1
            pipeline.addBefore("handler2", "handler1", new OutboundHandlerDemo());
            // 在handle2之后添加handler3
            pipeline.addAfter("handler2", "handler3", new OutboundHandlerDemo());

            // 删除一个handler
            pipeline.remove("handler3");
            // 替换一个handler
            pipeline.replace("handler1", "handlerFirst", new OutboundHandlerDemo());
        }
    }

    // 入站和出站混合
    static class MixInAndOutChannelInitializer extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            // 获取pipeline
            ChannelPipeline pipeline = ch.pipeline();

            // 第一个添加StringDecoder，用于解码信息
            pipeline.addFirst(new StringDecoder());

            // 第二个、第三个Handler添加入站处理器
            pipeline.addLast("In handler1", new InboundHandlerDemo());
            pipeline.addLast("In handler2", new InboundHandlerDemo());

            // 第四个Handler添加出站处理器
            pipeline.addLast("Out handler3", new OutboundHandlerDemo());

            // 第五个Handler添加入站处理器
            pipeline.addLast("In handler4", new InboundHandlerDemo());

            // 最后一个是出站处理器
            pipeline.addLast("Out handler5", new OutboundHandlerDemo());
        }
    }
}