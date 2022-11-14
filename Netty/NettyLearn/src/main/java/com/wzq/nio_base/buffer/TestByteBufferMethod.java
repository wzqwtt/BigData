package com.wzq.nio_base.buffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 试用ByteBuffer的方法
 *
 * @author wzq
 * @create 2022-11-14 19:14
 */
@Slf4j
public class TestByteBufferMethod {

    public static void main(String[] args) {
        // 构造一个ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 向buffer中写入1个字节的数据
        buffer.put((byte) 97);
        // 使用ByteBufferUtil工具类，查看buffer状态
        ByteBufferUtil.debugAll(buffer);

        // 向buffer中写入4个字节的数据
        buffer.put(new byte[]{98, 99, 100, 101});
        ByteBufferUtil.debugAll(buffer);

        // 获取数据
        buffer.flip();
        // flip之后打印三个属性的值
        log.debug("capacity:{},limit:{},position:{}", buffer.capacity(), buffer.limit(), buffer.position());
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);

        // 使用mark和reset标记
        buffer.mark();
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        buffer.reset();
        ByteBufferUtil.debugAll(buffer);

        // 使用compact切换到写模式
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);

        // 再次写入值
        buffer.put((byte) 102);
        buffer.put((byte) 103);
        ByteBufferUtil.debugAll(buffer);
    }
}
