package com.wzq.chatroom.server.handler;

import com.wzq.chatroom.server.session.Session;
import com.wzq.chatroom.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理退出请求
 *
 * @author wzq
 * @create 2022-11-26 13:51
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 连接断开时，触发Inactive事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = SessionFactory.getSession();
        // 从当前会话移除channel
        session.unbind(ctx.channel());

        log.debug("{}已经断开", ctx.channel());
    }

    // 异常断开

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Session session = SessionFactory.getSession();
        // 从当前会话移除channel
        session.unbind(ctx.channel());

        log.debug("{}异常断开，原因是:{}", ctx.channel(), cause.getCause());
    }
}
