package com.wzq.lockSupports;

import java.util.concurrent.TimeUnit;

/**
 * 演示Object类中的wait()和notify()方法实现线程的等待和唤醒
 * <p>
 * m1方法：正常情况下使用wait和notify
 * <p>
 * m2方法：不在同步代码块中使用wait和notify方法，将会报错
 * <p>
 * m3方法：notify在wait前先执行，程序永远不会停止
 *
 * @author wzq
 * @create 2022-10-23 10:15
 */
public class ObjectWaitAndNotify {

    public static void main(String[] args) {
        m1();   // 正常情况下使用wait和notify
    }

    // notify在wait前先执行，程序永远不会停止
    private static void m3() {
        Object objectLock = new Object();
        new Thread(() -> {
            // 暂停1秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + "\t----come in");

                try {
                    // 线程等待，如果该线程阻塞，其他线程可以持有锁
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 线程被唤醒之后，打印一句话
                System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
            }

        }, "t1").start();

        new Thread(() -> {
            synchronized (objectLock) {
                // 唤醒持有ObjectLock锁的t1线程
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t----唤醒其他线程");
            }
        }, "t2").start();
    }

    // 不在同步代码块中使用wait和notify方法，将会报错
    private static void m2() {
        Object objectLock = new Object();
        // 新建一个线程t1
        new Thread(() -> {
//            synchronized (objectLock) {
            System.out.println(Thread.currentThread().getName() + "\t----come in");

            try {
                // 线程等待，如果该线程阻塞，其他线程可以持有锁
                objectLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 线程被唤醒之后，打印一句话
            System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
//            }
        }, "t1").start();

        // 暂停1秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
//            synchronized (objectLock) {
            // 唤醒持有objectLock这把锁的线程
            objectLock.notify();
            System.out.println(Thread.currentThread().getName() + "\t----唤醒其他线程");
//            }
        }, "t2").start();
    }

    // 正常情况下使用wait和notify
    private static void m1() {
        Object objectLock = new Object();
        // 新建一个线程t1
        new Thread(() -> {
            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + "\t----come in");

                try {
                    // 线程等待，如果该线程阻塞，其他线程可以持有锁
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 线程被唤醒之后，打印一句话
                System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
            }
        }, "t1").start();

        // 暂停1秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            synchronized (objectLock) {
                // 唤醒持有objectLock这把锁的线程
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t----唤醒其他线程");
            }
        }, "t2").start();
    }

}
