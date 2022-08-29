package com.wzq.nio;

import java.nio.ByteBuffer;

/**
 * 只读Buffer
 *
 * @author wzq
 * @create 2022-08-28 22:23
 */
public class ReadOnlyBuffer {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        for (int i = 0; i < 64; i++) {
            byteBuffer.put((byte) i);
        }

        byteBuffer.flip();

        // 得到一个只读Buffer
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());
    }

}
