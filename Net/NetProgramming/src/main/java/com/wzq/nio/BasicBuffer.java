package com.wzq.nio;

import java.nio.IntBuffer;

/**
 * 举例说明 Buffer的使用（简单案例）
 *
 * @author wzq
 * @create 2022-08-28 19:13
 */
public class BasicBuffer {

    public static void main(String[] args) {
        // 创建一个buffer，大小为5，即可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        // 向Buffer中存放数据
//        intBuffer.put(10);
//        intBuffer.put(11);
//        intBuffer.put(12);
//        intBuffer.put(13);
//        intBuffer.put(14);

        // capacity 容量有多大
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        // 如何从Buffer读数据
        // 将buffer转换，读写切换！一定要有！
        intBuffer.flip();

        // buffer还有内容吗
        while (intBuffer.hasRemaining()) {
            // get里面维护了一个索引
            System.out.println(intBuffer.get());
        }
    }

}
