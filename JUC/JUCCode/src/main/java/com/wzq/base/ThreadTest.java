package com.wzq.base;

/**
 * 多线程的创建，方式一：继承于Thread类
 * 1、创建一个类继承于Thread类的子类
 * 2、重写Thread类中的run()方法 --> 此线程执行的操作声明在run方法中
 * 3、创建Thread类的子类的对象
 * 4、通过此对象去调用start()方法
 * <p>
 * 例子：遍历100以内的所有偶数
 *
 * @author wzq
 * @create 2022-08-02 21:25
 */


// 1、创建一个类继承于Thread类的子类
class MyThread extends Thread {

    // 2、重写Thread类中的run()方法
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
}

public class ThreadTest {
    public static void main(String[] args) {
        // 3、创建Thread类的子类的对象
        MyThread t1 = new MyThread();

        // 4、通过此对象去调用start()方法: 1、启动当前线程；2、调用当前线程的run方法
        t1.start();

//        // 第三个线程
//        MyThread t2 = new MyThread();
//        t2.start();

        // 匿名子类的方式创建线程
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 2 == 0) {
                        System.out.println(Thread.currentThread().getName() + ":" + i);
                    }
                }
            }
        }.start();

        // 如下操作仍然在main方法（主线程）中执行
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
}