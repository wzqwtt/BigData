package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.message.LoginRequestMessage;
import com.wzq.chatroom.message.LoginResponseMessage;
import com.wzq.chatroom.server.service.UserServiceFactory;
import com.wzq.chatroom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 只关注LoginRequestMessage的Handler
 *
 * @author wzq
 * @create 2022-11-25 17:09
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();

        // 使用我们的业务代码判断是否登录成功
        boolean login = UserServiceFactory.getUserService().login(username, password);

        // 用于响应消息
        LoginResponseMessage loginResponseMessage;

        if (login) {
            // 建立用户与Channel的关系
            SessionFactory.getSession().bind(ctx.channel(), username);
            // 返回连接成功的响应信息
            loginResponseMessage = new LoginResponseMessage(true, "登录成功！");
        } else {
            // 登录失败
            loginResponseMessage = new LoginResponseMessage(false, "用户名或密码错误！");
        }

        // 返回登录结果
        ctx.writeAndFlush(loginResponseMessage);
    }
}
