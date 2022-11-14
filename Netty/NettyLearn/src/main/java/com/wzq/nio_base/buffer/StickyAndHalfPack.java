package com.wzq.nio_base.buffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 粘包与半包问题
 *
 * @author wzq
 * @create 2022-11-14 20:06
 */
@Slf4j
public class StickyAndHalfPack {

    public static void main(String[] args) {
        // 模拟粘包半包问题
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm wzqwtt\nHo".getBytes());
        split(source);  // 调用方法处理粘包半包问题
        source.put("w are you?\n".getBytes());
        split(source);  // 调用方法处理粘包半包问题
    }

    private static void split(ByteBuffer source) {
        // 切换到读模式
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整信息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从source读，向target写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact();
    }
}
