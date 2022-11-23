package com.wzq.netty.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * 测试使用ByteToMessageDecoder
 *
 * @author wzq
 * @create 2022-11-22 21:05
 */
@Slf4j
public class TestByteToMessageDecoder {

    public static void main(String[] args) {
        // 使用EmbeddedChannel测试Handler
        EmbeddedChannel channel = new EmbeddedChannel(
                // 编写一个ByteToMessageDecoder
                new ByteToMessageDecoder() {
                    // decode必须要实现
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        // 如果发送过来的数据大于等于4个字节，才可以读取一个int值
                        if (in.readableBytes() >= 4) {
                            // 使用out把数据推送到下游
                            out.add(in.readInt());
                        }
                    }
                },
                // 添加一个入站适配器，用于输出上游传递的信息
                new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug(String.valueOf(msg));
                    }
                }
        );

        // 向EmbeddedChannel发送测试数据
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 100; i++) {
            buf.writeInt(i);
        }
        channel.writeInbound(buf);
    }

}
