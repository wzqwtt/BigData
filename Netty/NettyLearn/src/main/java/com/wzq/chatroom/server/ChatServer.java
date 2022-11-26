package com.wzq.chatroom.server;

import com.wzq.chatroom.protocol.MessageCodecSharable;
import com.wzq.chatroom.protocol.ProtocolFrameDecoder;
import com.wzq.chatroom.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天室服务端类
 *
 * @author wzq
 * @create 2022-11-25 15:39
 */
@Slf4j
public class ChatServer {

    private final static int PORT = 7;

    public static void main(String[] args) {
        // Handler
        // 日志打印HANDLER
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        // 协议编解码
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        // 只关注登录信息的Handler
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        // 只关注ChatRequest的Handler
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        // 只关注GroupCreateRequestMessage的Handler，创建群组
        GroupCreateRequestMessageHandler CREATE_GROUP_HANDLER = new GroupCreateRequestMessageHandler();
        // 只关注往群聊发送消息的请求
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        // 只关注gmembers请求
        GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        // 只关注Join请求
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        // 只关注退出群聊请求
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        // 退出事件
        QuitHandler QUIT = new QuitHandler();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 长度帧解码器，不能被多个线程共享，所以需要单独new
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            // 协议编解码器
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            // 只关注登录信息的Handler
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            // 只关注ChatRequest的Handler
                            ch.pipeline().addLast(CHAT_HANDLER);
                            // 只关注创建群组的Handler
                            ch.pipeline().addLast(CREATE_GROUP_HANDLER);
                            // 只关注往群聊发送消息的请求
                            ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                            // 只关注gmembers请求
                            ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                            // 只关注Join请求
                            ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                            // 只关注退出群聊请求
                            ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                            // 退出
                            ch.pipeline().addLast(QUIT);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
