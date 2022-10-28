package com.wzq.io;

import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 转换流
 * <p>
 * 1、转换流：输入字符流
 * InputStreamReader：将一个字节的输入流转换为字符的输入流
 * OutputStreamWriter：将一个字符的输出流转换为字节的输出流
 * 2、作用：提供字节流与字符流之间的转换
 * 3、解码：字节、字节数组 ---> 字符数组、字符串
 * 编码：字符数组、字符串 ---> 字节、字节数组
 *
 * @author wzq
 * @create 2022-08-17 23:01
 */
public class ConvertStreamTest {

    @Test
    public void test1() {
        InputStreamReader isr = null;
        try {
            FileInputStream fis = new FileInputStream("file/words.txt");

            // 第一个参数InputStream，第二个参数指定字符集
//        InputStreamReader isr = new InputStreamReader(fis); // 不传递第二个参数，使用系统默认字符集
//            isr = new InputStreamReader(fis,"UTF-8");
            // 传递第二个参数“字符集”，可以使用常量类 StandardCharsets
            isr = new InputStreamReader(fis, StandardCharsets.UTF_8);

            char[] cbuf = new char[10];
            int len;
            while ((len = isr.read(cbuf)) != -1) {
                String str = new String(cbuf, 0, len);
                System.out.print(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void testUTFtoGBK() {
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        try {
            FileInputStream fis = new FileInputStream("file/words.txt");
            FileOutputStream fos = new FileOutputStream("file/wordsGBK.txt");

            isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            osw = new OutputStreamWriter(fos, "gbk");

            char[] cbuf = new char[10];
            int len;
            while ((len = isr.read(cbuf)) != -1) {
                osw.write(cbuf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
