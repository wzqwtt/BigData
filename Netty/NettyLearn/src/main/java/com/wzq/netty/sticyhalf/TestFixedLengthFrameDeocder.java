package com.wzq.netty.sticyhalf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;

/**
 * 测试定长解码器FixedLengthFrameDeocder
 *
 * @author wzq
 * @create 2022-11-24 16:14
 */
@Slf4j
public class TestFixedLengthFrameDeocder {

    public static void main(String[] args) {
        // 使用EmbeddedChannel进行测试
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(),
                new FixedLengthFrameDecoder(10)
        );

        // 发送一些数据
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        Random r = new Random();
        char c = '0';
        for (int i = 0; i < 10; i++) {
            buf.writeBytes(fill10Bytes(c++, r.nextInt(10) + 1));
            embeddedChannel.writeInbound(buf);
        }
    }

    public static byte[] fill10Bytes(char c, int len) {
        byte[] bytes = new byte[10];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) c;
            System.out.print(c);
        }
        for (int i = len; i < 10; i++) {
            bytes[i] = '_';
            System.out.print('_');
        }
        System.out.println();
        return bytes;
    }
}
