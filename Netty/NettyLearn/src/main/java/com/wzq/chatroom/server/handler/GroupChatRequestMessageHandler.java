package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.GroupChatRequestMessage;
import com.wzq.chatroom.message.GroupChatResponseMessage;
import com.wzq.chatroom.server.session.GroupSession;
import com.wzq.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 只关注往群聊发送信息的请求
 *
 * @author wzq
 * @create 2022-11-25 21:28
 */
@Slf4j
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        // 获取请求体的所有信息
        String content = msg.getContent();
        String groupName = msg.getGroupName();
        String from = msg.getFrom();

        // 判断组是否存在
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        boolean isCreated = groupSession.IsCreated(groupName);

        if (isCreated) {
            // 群聊存在，可以给所有成员发送消息
            GroupChatResponseMessage message = new GroupChatResponseMessage(true,
                    "[" + groupName + "] me: " + content);
            ctx.writeAndFlush(message);

            // 给其他成员发送消息
            message = new GroupChatResponseMessage(true,
                    "[" + groupName + "] " + from + ": " + content);
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            for (Channel channel : membersChannel) {
                // 排除自己
                if (channel != ctx.channel()) {
                    channel.writeAndFlush(message);
                }
            }
        } else {
            GroupChatResponseMessage message = new GroupChatResponseMessage(false,
                    "聊天群不存在，消息发送失败！");
            ctx.writeAndFlush(message);
        }

    }
}
