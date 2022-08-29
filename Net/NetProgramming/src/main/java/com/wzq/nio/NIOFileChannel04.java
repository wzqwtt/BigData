package com.wzq.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 案例4 拷贝文件 transferFrom方法
 * <p>
 * 1、使用FileChannel(通道)和方法transferFrom，完成文件的拷贝
 * <p>
 * 2、拷贝一张图片
 *
 * @author wzq
 * @create 2022-08-28 22:09
 */
public class NIOFileChannel04 {

    public static void main(String[] args) throws Exception {
        // 创建FileInputStream对象以及FileChannel对象
        FileInputStream fileInputStream = new FileInputStream("file/1.jpg");
        FileChannel srcChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("file/1Copy3.jpg");
        FileChannel destChannel = fileOutputStream.getChannel();

        // 使用transferFrom读取
        destChannel.transferFrom(srcChannel, 0, srcChannel.size());

        fileInputStream.close();
        fileOutputStream.close();
    }

}
