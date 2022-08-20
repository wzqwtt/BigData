package com.wzq.interrupt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程中断Demo，三种方法
 *
 * @author wzq
 * @create 2022-08-19 13:45
 */
public class InterruptDemo {


    public static void main(String[] args) {
//        testVolatile();   // 测试Volatile变量实现中断

//        testAtomicBoolean();  // 测试AtomicBoolean

        testInterrupt();    // 测试Interrupt中断
    }

    private static void testInterrupt() {
        Thread t1 = new Thread(() -> {
            while (true) {
                // 获取当前线程，监听本线程是否被中断
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "\t Interrupt，程序停止！");
                    break;
                }
                System.out.println("t1 -----------hello Interrupt");
            }
        }, "t1");
        t1.start();

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("t1线程的默认中断标识位：" + t1.isInterrupted());

        new Thread(() -> {
            t1.interrupt(); // 协商中断线程
        }, "t2").start();
    }

    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    private static void testAtomicBoolean() {
        new Thread(() -> {
            while (true) {
                if (atomicBoolean.get()) {
                    System.out.println(Thread.currentThread().getName() + "\t AtomicBoolean被修改为true，程序停止！");
                    break;
                }
                System.out.println("t1 -----------hello AtomicBoolean");
            }
        }, "t1").start();

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            atomicBoolean.set(true);
        }, "t2").start();
    }

    // volatile 具有可见性
    static volatile boolean isStop = false;

    private static void testVolatile() {
        new Thread(() -> {
            while (true) {
                if (isStop) {
                    System.out.println(Thread.currentThread().getName() + "\t isStop被修改为true，程序停止！");
                    break;
                }
                System.out.println("t1-----------hello,volatile");
            }
        }, "t1").start();

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            isStop = true;
        }, "t2").start();
    }

}
