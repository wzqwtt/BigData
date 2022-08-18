package com.wzq.io;

import org.junit.Test;

import java.io.*;

/**
 * 缓冲流测试
 * <p>
 * 缓冲流的意义就是：提高流读写速度
 *
 * @author wzq
 * @create 2022-08-17 22:38
 */
public class BufferedTest {

    @Test
    public void testCopyFile() {
        long startTime = System.currentTimeMillis();

        String srcPath = "file/What.If_S01E03.mp4";
        String destPath = "file/What.If_S01E03_Copy.mp4";
        copyFile(srcPath, destPath);

        long endTime = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime - startTime) + " 毫秒");   // 920 ms
    }

    /**
     * 复制文件
     *
     * @param srcPath  要复制的文件路径
     * @param destPath 复制文件目的地路径
     */
    public void copyFile(String srcPath, String destPath) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            // 1、造File类
            File srcFile = new File(srcPath);
            File destFile = new File(destPath);

            // 2、创建缓冲输入输出流
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);

            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            System.out.println("复制完成！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4、关闭资源，先关缓冲流，再关字节流；即先关外层，再关里面
            // 关闭外层流，内层自动关闭！！！
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
