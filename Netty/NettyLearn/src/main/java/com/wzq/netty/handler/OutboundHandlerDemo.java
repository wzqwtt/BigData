package com.wzq.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * 出站服务器
 *
 * @author wzq
 * @create 2022-11-19 19:29
 */
@Slf4j
public class OutboundHandlerDemo extends ChannelOutboundHandlerAdapter {
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.debug("出站Handler:{}", ctx.name());
        super.read(ctx);
    }
}
