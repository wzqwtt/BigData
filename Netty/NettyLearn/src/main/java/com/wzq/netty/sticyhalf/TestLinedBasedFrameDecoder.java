package com.wzq.netty.sticyhalf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 测试行解码器
 *
 * @author wzq
 * @create 2022-11-24 16:39
 */
@Slf4j
public class TestLinedBasedFrameDecoder {

    public static void main(String[] args) {
        // 使用EmbeddedChannel进行测试
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(),
                new LineBasedFrameDecoder(1024)
        );

        // 发送数据
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        char c = '0';
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            buf.writeBytes(makeString(c++, random.nextInt(10) + 1));
            embeddedChannel.writeInbound(buf);
        }
    }

    public static byte[] makeString(char c, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(c);
        }
        sb.append("\n");
        return sb.toString().getBytes();
    }

}
