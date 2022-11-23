package com.wzq.netty.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 测试使用ReplayingDecoder
 *
 * @author wzq
 * @create 2022-11-22 21:39
 */
@Slf4j
public class TestReplayingDecoder {

    public static void main(String[] args) {
        // 创建EmbeddedChannel用于测试
        EmbeddedChannel channel = new EmbeddedChannel(
                // 使用ReplayingDecoder，指定Integer为泛型
                new ReplayingDecoder<Integer>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        out.add(in.readInt());
                    }
                },
                new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug(String.valueOf(msg));
                    }
                }
        );

        // 发送一些数据进去
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 100; i++) {
            buf.writeInt(i);
        }
        channel.writeInbound(buf);
    }

}
