package com.wzq.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 案例1：本地文件写数据
 * <p>
 * 1、使用前面学习的ByteBuffer(缓冲)和FileChannel(通道)，将"hello,wzqwtt哈哈"写入到file/file01.txt中
 * <p>
 * 2、文件不存在就创建
 *
 * @author wzq
 * @create 2022-08-28 20:59
 */
public class NIOFileChannel01 {

    public static void main(String[] args) throws Exception {
        String str = "hello,wzqwtt哈哈";

        // 创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("input/file01.txt");

        // 通过FileOutputStream获取对应的FileChannel
        // 这个类型FileChannel，真实类型是 FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        // 创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes()); // 放入字节数组

        byteBuffer.flip();  // 读写翻转

        // 把Buffer的数据写到Channel
        fileChannel.write(byteBuffer);

        fileOutputStream.close();
    }
}
