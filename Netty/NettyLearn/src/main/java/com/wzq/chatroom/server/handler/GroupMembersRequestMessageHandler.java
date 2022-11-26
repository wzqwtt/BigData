package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.GroupMembersRequestMessage;
import com.wzq.chatroom.message.GroupMembersResponseMessage;
import com.wzq.chatroom.server.session.GroupSession;
import com.wzq.chatroom.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

import java.util.Set;

/**
 * 只关注GroupMembersRequestMessage发来的请求
 *
 * @author wzq
 * @create 2022-11-25 22:01
 */
@Data
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();

        // 获取groupSession
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        // 判断群聊是否存在
        boolean isCreated = groupSession.IsCreated(groupName);

        if (isCreated) {
            // 如果组存在，返回查询到的组成员
            Set<String> members = groupSession.getMembers(groupName);
            StringBuilder sb = new StringBuilder();
            members.stream().map(s -> sb.append(s + ","));
            GroupMembersResponseMessage message = new GroupMembersResponseMessage(true, "群聊" + groupName + "的成员有: [" + sb.toString() + "]");
            ctx.writeAndFlush(message);
        } else {
            GroupMembersResponseMessage message = new GroupMembersResponseMessage(false,
                    groupName + "群聊不存在！");
            ctx.writeAndFlush(message);
        }


    }
}
