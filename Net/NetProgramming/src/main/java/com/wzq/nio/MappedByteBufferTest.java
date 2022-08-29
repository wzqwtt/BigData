package com.wzq.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 1、MappedByteBuffer，可以让文件直接在内存（堆外内存）修改，操作系统不需要拷贝一次
 * <p>
 * 2、
 *
 * @author wzq
 * @create 2022-08-28 22:28
 */
public class MappedByteBufferTest {

    public static void main(String[] args) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("file/words.txt", "rw");
        // 获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();

        /**
         * 参数1：FileChannel.MapMode.READ_WRITE 使用读写模式
         * 参数2：0，可以直接修改的起始位置
         * 参数3：5，是映射到内存的大小，即将文件多少个字节映射到内存
         * 可以直接修改的范围是：[0,5)
         * 实际类型是：DirectByteBuffer
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0,(byte) 'H');
        mappedByteBuffer.put(3,(byte) '9');
        // mappedByteBuffer.put(5,(byte) '9'); // 会抛出异常

        randomAccessFile.close();

        System.out.println("修改成功~~~");
    }

}
