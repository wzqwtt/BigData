package com.wzq.netty.handler;

import io.netty.channel.*;

/**
 * @author wzq
 * @create 2022-11-19 20:26
 */
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        方法一：使用ChannelFuture
//        ChannelFuture future = ctx.channel().writeAndFlush("wzq");
//        future.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (!future.isSuccess()) {
//                    // 如果没有执行成功就打印报错
//                    future.cause().printStackTrace();
//                    future.channel().close();
//                }
//            }
//        });

        // 方法二：使用Promise
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    // 如果没有执行成功就打印报错
                    future.cause().printStackTrace();
                    future.channel().close();
                }
            }
        });

        super.write(ctx, msg, promise);
    }
}
