package com.wzq.io;

import lombok.SneakyThrows;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 一、流的分类
 * 1、操作数据单位：字节流、字符流
 * 2、数据的流向：输入流、输出流
 * 3、流的角色：节点流、处理流
 * <p>
 * 二、流的体系结构
 * <table>
 *     <tr><th>抽象基类</th> <th>节点流</th> <th>缓冲流（处理流的一种）</th> </tr>
 *     <tr> <td>InputStream</td> <td>FileInputStream</td> <td>BufferedInputStream</td></tr>
 *     <tr> <td>OutputStream</td> <td>FileOutputStream</td> <td>BufferedOutputStream</td></tr>
 *     <tr> <td>Reader</td> <td>FileReader</td> <td>BufferReader</td></tr>
 *     <tr> <td>Writer</td> <td>FileWriter</td> <td>BufferWriter</td></tr>
 * </table>
 *
 * @author wzq
 * @create 2022-08-16 22:52
 */
public class FileReaderWriterTest {

    private final String testFilePath = "file/words.txt";

    /**
     * 将Input文件夹下的words.txt内容读入到程序中，并输出到控制台
     * <p>
     * 1、read方法：返回读入的一个字符，如果达到文件末尾返回-1
     * 2、异常的处理：为了保证流资源一定可以执行关闭操作。需要使用try-catch-finally处理
     * 3、读入的文件一定要存在，否则会报FileNotFountException
     */
    @Test
    public void testFileReader() {
        FileReader fr = null;
        try {
            // 1、实例化File类对象，指明要操作的文件
            File file = new File(testFilePath);

            // 2、提供具体的流
            fr = new FileReader(file);

            // 3、数据读入
            // read方法返回一个读入的字符。如果到达文件末尾，返回-1
//        int data = fr.read();
//        while (data != -1) {
//            System.out.print((char) data);
//            data = fr.read();
//        }

            int data;
            while ((data = fr.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4、关闭流
            try {
                if (fr != null) fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 测试read(char[] cbuf)
     */
    @Test
    public void testFileReader1() {
        FileReader fileReader = null;
        try {
            // 1、新建File类，传入路径
            File file = new File(testFilePath);
            // 2、创建FileReader实例化对象
            fileReader = new FileReader(file);
            // 3、读取字符
            // read(char[] cbuf): 返回每次读入cbuf数组中字符的个数。如果达到文件末尾，返回-1
            char[] cbuf = new char[7];
            int len;
            while ((len = fileReader.read(cbuf)) != -1) {
                for (int i = 0; i < len; i++) {
                    System.out.print(cbuf[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4、关闭资源
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从内存中写出数据到硬盘文件里
     * <p>
     * 说明：
     * 1、输出操作，对应的File可以不存在。并不会报异常
     * 2、File对应的硬盘中的文件如果不存在，在输出过程中，会自动创建此文件
     * File对应的硬盘中的文件如果存在：
     * 如果流使用的构造器是：FileWrite(file,false) / FileWriter(file): 对原文件覆盖
     * 如果流使用的构造器是：FileWrite(file,true): 对原文件进行追加写
     */
    @Test
    public void testFileWriter() {
        FileWriter fw = null;
        try {
            // 1、提供File类对象，指明写出到的文件
            File file = new File("file/words1.txt");
            // 2、提供FileWriter对象，用于数据的写出
            fw = new FileWriter(file);
            // 3、写数据出去
            String str = "Hello File Writer! \nHello Java!";
            fw.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                // 4、关闭流
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 文件的复制
     * 但不能使用字符流处理图片数据
     */
    @Test
    public void testFileCopy() {
        FileReader fr = null;
        FileWriter fw = null;
        try {
            File srcFile = new File("file/words.txt");
            File destFile = new File("file/wordsCopy.txt");

            fr = new FileReader(srcFile);
            fw = new FileWriter(destFile);

            char[] cbuf = new char[10];
            int len;
            while ((len = fr.read(cbuf)) != -1) {
                fw.write(cbuf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 如果fr出问题，fw也要顺利关掉
            try {
                if (fw != null) {
                    fw.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
