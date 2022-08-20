package com.wzq.io;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 测试FileInputStream和FileOutputStream的使用
 * <p>
 * 注意：
 * 1、对于文本文件(.txt,.java,.c,.cpp...)，使用字符流处理
 * 2、对于非文本文件(.jpg,.mp3,.mp4,.avi,.doc,.ppt...)，使用字节流处理
 *
 * @author wzq
 * @create 2022-08-17 22:08
 */
public class FileInputOutputStreamTest {

    @Test
    public void testFileInputStream() {
        FileInputStream fis = null;
        try {
            // 1、造File
            File file = new File("file/words1.txt");

            // 2、造FileInputStream
            fis = new FileInputStream(file);

            // 3、输出
            byte[] buffer = new byte[5];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                String str = new String(buffer, 0, len);
                System.out.print(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                // 4、关闭资源
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testCopyFile() {
        long startTime = System.currentTimeMillis();

        String srcPath = "file/What.If_S01E03.mp4";
        String destPath = "file/What.If_S01E03Copy.mp4";
        copyFile(srcPath, destPath);

        long endTime = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime - startTime) + " 毫秒");   // 3792 ms
    }

    /**
     * 字节流复制图片
     */
    public void copyFile(String srcPath, String destPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            // 1、指定源文件和要复制到哪个文件
            File srcFile = new File(srcPath);
            File destFile = new File(destPath);

            // 2、造字节流对象
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);

            // 3、复制图片
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("文件复制成功！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4、关闭资源
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
