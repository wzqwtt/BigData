package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.GroupQuitRequestMessage;
import com.wzq.chatroom.message.GroupQuitResponseMessage;
import com.wzq.chatroom.server.session.Group;
import com.wzq.chatroom.server.session.GroupSession;
import com.wzq.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 关注退出群聊的请求
 *
 * @author wzq
 * @create 2022-11-26 13:44
 */
@Slf4j
@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {

        String groupName = msg.getGroupName();
        String username = msg.getUsername();

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.removeMember(groupName, username);

        GroupQuitResponseMessage message = null;
        if (group == null) {
            // 组不存在，退出失败
            message = new GroupQuitResponseMessage(false,
                    groupName + "群聊不存在，退出失败!");
        } else {
            message = new GroupQuitResponseMessage(true, "退出" + groupName + "群聊成功!");
        }
        ctx.writeAndFlush(message);

    }
}
