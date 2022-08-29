package com.wzq.nio;

import java.nio.ByteBuffer;

/**
 * @author wzq
 * @create 2022-08-28 22:18
 */
public class NIOByteBufferPutGet {

    public static void main(String[] args) {
        // 创建一个Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        // 类型化放入数据
        byteBuffer.putInt(19);
        byteBuffer.putLong(19L);
        byteBuffer.putChar('王');

        byteBuffer.flip();

        // 取数据必须一一对应，否则会有BufferUnderFlowException
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());

        // 错误示范
//        byteBuffer.flip();
//        System.out.println(byteBuffer.getInt());
//        System.out.println(byteBuffer.getInt());
//        System.out.println(byteBuffer.getLong());
    }

}
