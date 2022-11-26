package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.GroupJoinRequestMessage;
import com.wzq.chatroom.message.GroupJoinResponseMessage;
import com.wzq.chatroom.server.session.Group;
import com.wzq.chatroom.server.session.GroupSession;
import com.wzq.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 只关注join request
 *
 * @author wzq
 * @create 2022-11-26 0:00
 */
@Slf4j
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {

        String groupName = msg.getGroupName();
        String username = msg.getUsername();

        // 判断群聊是否存在
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.joinMember(username, groupName);

        GroupJoinResponseMessage message = null;

        if (group == null) {
            message = new GroupJoinResponseMessage(false, groupName + "群聊不存在！");
        } else {
            message = new GroupJoinResponseMessage(true, username + "加入[" + groupName + "]成功");
        }
        ctx.writeAndFlush(message);
    }
}
