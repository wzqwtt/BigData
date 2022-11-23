package com.wzq.netty.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 测试TooLongFrameException
 *
 * @author wzq
 * @create 2022-11-22 22:01
 */
@Slf4j
public class TestTooLongFrameException {

    // 最大帧数
    private static final int MAX_FRAME_SIZE = 32;

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new ByteToMessageDecoder() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        int readable = in.readableBytes();
                        // 检测缓冲区中是否有超过MAX_FRAME_SIZE个字节
                        if (readable > MAX_FRAME_SIZE) {
                            // 跳过所有可读字节，并抛出异常
                            in.skipBytes(readable);
                            throw new TooLongFrameException("Frame too big!");
                        }
                    }
                }
        );

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 10; i++) {
            buf.writeInt(i);
        }
        channel.writeInbound(buf);
    }

}
