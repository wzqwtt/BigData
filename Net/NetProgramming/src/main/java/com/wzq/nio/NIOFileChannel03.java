package com.wzq.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 案例3：使用一个Buffer完成文件读取
 * <p>
 * 1、使用FileChannel和方法read，write，完成文件的拷贝
 * <p>
 * 2、拷贝一个文本文件1.txt，放在项目input目录下
 *
 * @author wzq
 * @create 2022-08-28 21:35
 */
public class NIOFileChannel03 {

    public static void main(String[] args) throws Exception {

        // 创建FileInputStream和FileOutputStream对象，以及他们的Channel对象
        FileInputStream fileInputStream = new FileInputStream("input/words.txt");
        FileChannel fileInputStreamChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("input/wordsCopy.txt");
        FileChannel fileOutputStreamChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        // 循环读取
        while (true) {
            // 把Buffer复位，“清空”数据，！！！！！
            byteBuffer.clear();
            // fileInputStreamChannel读到Channel
            int read = fileInputStreamChannel.read(byteBuffer);
            // 如果read==-1，说明已经读取结束
            if (read == -1) {
                break;
            }
            // 写到fileOutputStreamChannel
            byteBuffer.flip();  // 读写翻转
            fileOutputStreamChannel.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }

}
