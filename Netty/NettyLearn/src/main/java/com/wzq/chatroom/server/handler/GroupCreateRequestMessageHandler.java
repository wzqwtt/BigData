package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.GroupCreateRequestMessage;
import com.wzq.chatroom.message.GroupCreateResponseMessage;
import com.wzq.chatroom.server.session.Group;
import com.wzq.chatroom.server.session.GroupSessionFactory;
import com.wzq.chatroom.server.session.Session;
import com.wzq.chatroom.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author wzq
 * @create 2022-11-25 21:02
 */
@Slf4j
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        // 拿请求过来的信息
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();

        // 创建群组
        Group group = GroupSessionFactory.getGroupSession().createGroup(groupName, members);

        if (group == null) {
            // 组创建成功
            // 给创建者发送创建成功消息
            GroupCreateResponseMessage message = new GroupCreateResponseMessage(true, groupName + "群聊创建成功");
            ctx.writeAndFlush(message);

            // 给被拉入群聊的用户发送信息
            message = new GroupCreateResponseMessage(true, "您已被拉入" + groupName + "群聊！");
            Session session = SessionFactory.getSession();

            for (String member : members) {
                // 获取所有成员对应的Channel
                Channel channel = session.getChannel(member);
                // 如果不是发起请求的成员，就发送响应
                if (channel != ctx.channel()) {
                    channel.writeAndFlush(message);
                }
            }

        } else {
            // 组创建失败
            GroupCreateResponseMessage message = new GroupCreateResponseMessage(false,
                    groupName + "群聊已存在，创建失败");
            ctx.writeAndFlush(message);
        }
    }
}
