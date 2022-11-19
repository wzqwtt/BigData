package com.wzq.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wzq
 * @create 2022-11-19 17:18
 */
@Slf4j
public class InboundHandlerDemo extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("入站Handler:{}, 消息:{}", ctx.name(), msg.toString());
        super.channelRead(ctx, msg);
    }
}
