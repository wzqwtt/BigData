package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.ChatRequestMessage;
import com.wzq.chatroom.message.ChatResponseMessage;
import com.wzq.chatroom.server.session.SessionFactory;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 只关注ChatRequestMessage
 *
 * @author wzq
 * @create 2022-11-25 20:19
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        // 拿到所有内容
        String to = msg.getTo();
        String content = msg.getContent();
        String from = msg.getFrom();

        // 拿到to对应的Channel
        Channel toChannel = SessionFactory.getSession().getChannel(to);

        // 判断是否在线
        if (toChannel != null) {
            // 在线
            ChatResponseMessage responseMessage = new ChatResponseMessage(true, from + ": " + content);
            responseMessage.setFrom(from);
            responseMessage.setContent(content);
            toChannel.writeAndFlush(responseMessage);

            // 给自己也回复一条消息
            responseMessage = new ChatResponseMessage(true, "To " + to + ": " + content);
            ctx.writeAndFlush(responseMessage);
        } else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或离线，发送失败！"));
        }
    }
}
