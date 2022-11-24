package com.wzq.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;

/**
 * Http服务端
 *
 * @author wzq
 * @create 2022-11-24 20:21
 */
@Slf4j
public class TestHttpServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());
                            // 加上一个HttpServer编解码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            // 查看HttpServerCodec解析出来的数据格式是什么
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("{}", msg.getClass());
                                    // 打印出来，HttpCodec会将请求拆分为两个格式的数据：
                                    // - DefaultHttpRequest  继承自 HttpRequest
                                    // - LastHttpContent$1   继承自 HttpContent

                                    // 接下来应该分别处理信息
                                    if (msg instanceof HttpRequest) {
                                        log.debug("处理DefaultHttpRequest格式信息");
                                    } else if (msg instanceof HttpContent) {
                                        log.debug("处理LastHttpContent格式信息");
                                    }

                                    super.channelRead(ctx, msg);
                                }
                            });
                            // 如果只对某个数据格式感兴趣，可以使用SimpleChannelInboundHandler
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                    // 获取请求
                                    log.debug(msg.uri());

                                    // 返回响应需要一个DefaultFullHttpResponse对象
                                    // 该对象需要两个参数
                                    // - HttpVersion：Http协议版本
                                    // - HttpResponseStatus：响应的状态码
                                    DefaultFullHttpResponse response =
                                            new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);

                                    // 添加返回内容
                                    byte[] bytes = "<h1>hello,world</h1>".getBytes();

                                    // 如果不指定响应长度，那么浏览器会一直尝试接收信息。浏览器会认为消息没有接收完
                                    response.headers().setInt(CONTENT_LENGTH, bytes.length);

                                    response.content().writeBytes(bytes);

                                    //写回响应
                                    ctx.writeAndFlush(response);
                                }
                            });
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.debug("server error");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
