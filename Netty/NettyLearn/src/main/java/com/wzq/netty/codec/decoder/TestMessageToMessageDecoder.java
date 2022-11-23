package com.wzq.netty.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 测试MessageToMessageDecoder
 *
 * @author wzq
 * @create 2022-11-22 21:51
 */
@Slf4j
public class TestMessageToMessageDecoder {

    public static void main(String[] args) {
        // 使用EmbeddedChannel进行测试
        EmbeddedChannel channel = new EmbeddedChannel(
                // 使用ReplayingDecoder将消息转换为Integer
                new ReplayingDecoder<Integer>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        out.add(in.readInt());
                    }
                },
                // 使用MessageToMessageDecoder将Integer转换为String
                new MessageToMessageDecoder<Integer>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
                        out.add(String.valueOf(msg));
                    }
                },
                // 一个普通的Handler
                new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug(String.valueOf(msg.getClass()));
                    }
                }
        );

        // 向channel中添加一些数据
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 100; i++) {
            buf.writeInt(i);
        }
        channel.writeInbound(buf);
    }

}
