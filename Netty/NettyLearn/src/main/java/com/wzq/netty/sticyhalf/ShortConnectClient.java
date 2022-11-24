package com.wzq.netty.sticyhalf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 短链接解决粘包问题
 *
 * @author wzq
 * @create 2022-11-24 15:41
 */
@Slf4j
public class ShortConnectClient {

    // 定义HOST和PORT常量
    private static final String HOST = "localhost";
    private static final int PORT = 7;

    public void send() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer(16);
                                    buf.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
                                    ctx.writeAndFlush(buf);
                                    // 在此处关闭此次连接！
                                    log.debug("关闭连接");
                                    ctx.channel().close();
                                }
                            });
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();

            // 关闭连接的异步事件
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        ShortConnectClient client = new ShortConnectClient();
        // 连续发送10次
        for (int i = 0; i < 10; i++) {
            client.send();
        }
    }

}
