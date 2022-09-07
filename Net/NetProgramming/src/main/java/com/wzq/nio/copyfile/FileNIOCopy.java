package com.wzq.nio.copyfile;

import com.wzq.util.IOUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用FileChannel复制文件
 *
 * @author wzq
 * @create 2022-08-31 21:10
 */
public class FileNIOCopy {

    public static void main(String[] args) {
        nioCopyResuorceFile();
    }

    public static void nioCopyResuorceFile() {
        String srcPath = "input/words.txt";
        String destPath = "input/wordsCopy1.txt";
        FileOutputStream fileOutputStream = null;
        FileChannel outChannel = null;
        FileInputStream fileInputStream = null;
        FileChannel inputChannel = null;

        try {
            // 获取输入输出流以及他们的channel
            fileOutputStream = new FileOutputStream(destPath);
            outChannel = fileOutputStream.getChannel();

            fileInputStream = new FileInputStream(srcPath);
            inputChannel = fileInputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len;
            while ((len = inputChannel.read(buffer)) != -1) {
                buffer.flip();  // 切换为读模式
                int outlength = 0;
                while ((outlength = outChannel.write(buffer)) != 0) {
                    System.out.println("写入的字节数：" + outlength);
                }
                buffer.clear(); // 写
            }
            outChannel.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            IOUtil.closeQuitely(inputChannel);
            IOUtil.closeQuitely(outChannel);
            IOUtil.closeQuitely(fileInputStream);
            IOUtil.closeQuitely(fileOutputStream);
        }

    }


}
