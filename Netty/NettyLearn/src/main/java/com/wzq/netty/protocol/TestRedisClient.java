package com.wzq.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟Redis客户端
 * <p>
 * # Redis会把一条命令看作一个数组，以“空格”分割
 * # 第一条要发的消息就是 *加数组的长度
 * *3
 * # 接下来发每个字段的长度与内容
 * # 第一个字段3个字符：$3，换行之后填充 set
 * $3
 * set
 * # 第二个字段4个字符：$4，换行之后把内容写上去
 * $4
 * name
 * # 第三个字段6个字符：$6，换行之后把内容写上去
 * $6
 * wzqwtt
 *
 * @author wzq
 * @create 2022-11-24 19:21
 */
@Slf4j
public class TestRedisClient {

    private static final String HOST = "localhost";
    private static final int PORT = 6379;
    // 行
    private static final byte[] LINE = {13, 10};

    private static void sendToRedis(String request) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());
                            // 将RedisClientHandler绑定到pipeline
                            ch.pipeline().addLast(new RedisClientHandler(request));
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.debug("client error");
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        sendToRedis("set name wzqwtt");
    }

    // RedisClient的Handler
    private static class RedisClientHandler extends ChannelInboundHandlerAdapter {

        private String command;

        public RedisClientHandler(String command) {
            this.command = command;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 新建一个ByteBuf
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

            // TODO 判断消息是否合法

            log.debug("command = {}", command);
            // 解析消息开始填充
            String[] request = command.split(" ");
            // *3, 数组长度
            buf.writeBytes(("*" + request.length).getBytes());
            buf.writeBytes(LINE);

            for (int i = 0; i < request.length; i++) {
                // $n，第i个指令的长度
                buf.writeBytes(("$" + request[i].length()).getBytes());
                buf.writeBytes(LINE);
                // content，第i条指令内容
                buf.writeBytes(request[i].getBytes());
                buf.writeBytes(LINE);
            }

            // 将ByteBuf发送出去
            ctx.writeAndFlush(buf);

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            log.debug(buf.toString());

            // 结束程序
            ctx.channel().close();
        }
    }

}
