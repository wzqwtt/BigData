package com.wzq.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 案例2：本地文件读
 * <p>
 * 1、使用ByteBuffer和FileChannel，将input/file01.txt中的数据读入到程序，并显示在控制台屏幕
 * <p>
 * 2、假定文件已存在
 *
 * @author wzq
 * @create 2022-08-28 21:19
 */
public class NIOFileChannel02 {
    public static void main(String[] args) throws Exception {
        // 创建文件输入流
        File file = new File("input/file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        // 通过FileInputStream获得对应的FileChannel，FileChannle实际的类型是FileChannelImpl
        FileChannel fileChannel = fileInputStream.getChannel();

        // 创建一个字节缓冲区，开辟一个文件大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        // 将通道的数据读取到Buffer
        fileChannel.read(byteBuffer);
        // 读写翻转
        byteBuffer.flip();

        // 将字节转换为String
        System.out.println(new String(byteBuffer.array()));
        // 关闭流
        fileInputStream.close();
    }
}
