package com.wzq.chatroom.client;

import com.wzq.chatroom.client.handler.MonitorHandler;
import com.wzq.chatroom.protocol.MessageCodecSharable;
import com.wzq.chatroom.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wzq
 * @create 2022-11-25 16:58
 */
@Slf4j
public class ChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 7;

    // handler
    static LoggingHandler LOGGING_HANDLER = new LoggingHandler();
    static MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatChannelInit());

            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.debug("client error!");
        } finally {

            group.shutdownGracefully();
        }
    }

    /**
     * ChatChannelInitializer
     */
    private static class ChatChannelInit extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline().addLast("PROTOCOL_DECODER", new ProtocolFrameDecoder());
//            ch.pipeline().addLast("LOGGING_HANDLER", LOGGING_HANDLER);
            ch.pipeline().addLast("MESSAGE_CODEC", MESSAGE_CODEC);
            // 添加Handler监听用户输入
            ch.pipeline().addLast("MONITOR_HANDLER", new MonitorHandler());
        }
    }


}
