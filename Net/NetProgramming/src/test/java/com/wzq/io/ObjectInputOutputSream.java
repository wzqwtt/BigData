package com.wzq.io;

import org.junit.Test;

import java.io.*;

/**
 * 对象流的使用
 * <p>
 * 1、ObjectInputStream
 * 2、ObjectOutputStream
 * <p>
 * 作用：用于存储和读取基本数据类型数据或对象的处理流
 * 它的强大之处就是可以把Java中的对象写到磁盘上，也可以读到内存中
 * <p>
 * 典型的应用就是：序列化/反序列化机制，网络传输中经常使用
 * <p>
 * 要想一个java对象是可序列化的，需要满足相应的要求，见Person.java
 *
 * @author wzq
 * @create 2022-08-17 23:59
 */
public class ObjectInputOutputSream {

    /**
     * 序列化过程：将内存中的java对象保存到磁盘中或通过网络传输出去
     * 使用ObjectOutputStream
     */
    @Test
    public void testObjectOutputStream() {
        ObjectOutputStream oos = null;
        try {
            // 1、造对象
            oos = new ObjectOutputStream(new FileOutputStream("file/object.dat"));

            // 2、写
            oos.writeObject(new String("我爱北京天安门"));
            oos.flush();    // 刷新操作

            // 写一个Person对象进去
            oos.writeObject(new Person("wzq", 5));
            oos.flush();

            oos.writeObject(new Person("wtt", 5, new Account(10000)));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 3、关资源
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 反序列化：将磁盘文件中的对象还原为内存中的一个java对象
     * 使用ObjectInputStream
     */
    @Test
    public void testObjectInputStream() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream("file/object.dat"));

            String object = (String) ois.readObject();
            System.out.println(object);

            Person p = (Person) ois.readObject();
            System.out.println(p);

            Person p1 = (Person) ois.readObject();
            System.out.println(p1);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
