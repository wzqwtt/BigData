package com.wzq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author wzq
 * @create 2022-11-21 14:02
 */
@Slf4j
public class TestByteBuf {
    public static void main(String[] args) {
        demo2();
    }

    // 创建非池化堆内存 与 创建非池化直接内存
    private static void demo2() {
        // 创建非池化堆内存
        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        System.out.println("非池化堆内存：" + heapBuf.getClass());

        // 创建非池化直接内存
        ByteBuf directBuf = ByteBufAllocator.DEFAULT.directBuffer();
        // 直接内存是默认的，等价于：
        // ByteBuf directBuf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("非池化直接内存：" + directBuf.getClass());
    }

    // 创建池化堆内存 与 创建池化直接内存
    private static void demo1() {
        // 创建池化堆内存
        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        System.out.println("池化堆内存：" + heapBuf.getClass());

        // 创建池化直接内存
        ByteBuf directBuf = ByteBufAllocator.DEFAULT.directBuffer();
        // 直接内存是默认的，等价于：
        // ByteBuf directBuf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("池化直接内存：" + directBuf.getClass());
    }

    // 创建一个ByteBuf
    private static void createByteBuf() {
        // 创建ByteBuf
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        MyByteBufUtil.log(buf);

        // 填充16个字节的数据
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append('a');
        }
        buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));

        // 打印查看
        MyByteBufUtil.log(buf);
    }
}
