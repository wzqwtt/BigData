package com.wzq.locks;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示可重入锁
 *
 * reEntry1方法演示synchronized方法
 * reEntry2方法演示synchronized代码块
 * main方法演示lock
 *
 * @author wzq
 * @create 2022-08-15 22:20
 */
public class ReEntryLockDemo {

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {

        // 加锁几次就要解锁几次！！！！
        new Thread(() -> {
            lock.lock();    // 外层上锁
            try {
                System.out.println(Thread.currentThread().getName() + " --- 外层调用");
                lock.lock();    // 中层上锁
                try {
                    System.out.println(Thread.currentThread().getName() + " --- 中层调用");
                    lock.lock();    // 内层上锁
                    try {
                        System.out.println(Thread.currentThread().getName() + " --- 内层调用");
                    } finally {
                        lock.unlock();  // 内层解锁
                    }
                } finally {
                    lock.unlock();  // 中层解锁
                }
            } finally {
                lock.unlock();  // 外层解锁
            }
        }, "t1").start();

    }

    private static void reEntrym2() {
        ReEntryLockDemo reEntryLockDemo = new ReEntryLockDemo();
        new Thread(() -> {
            reEntryLockDemo.m1();
        }, "t1").start();
    }

    public synchronized void m1() {
        System.out.println(Thread.currentThread().getName() + " --- m1 come in");
        m2();
        System.out.println(Thread.currentThread().getName() + " --- m1 end");
    }

    public synchronized void m2() {
        System.out.println(Thread.currentThread().getName() + " --- m2 come in");
        m3();
        System.out.println(Thread.currentThread().getName() + " --- m2 end");
    }

    public synchronized void m3() {
        System.out.println(Thread.currentThread().getName() + " --- m3 come in");
        System.out.println(Thread.currentThread().getName() + " --- m3 end");
    }

    private static void reEntryM1() {
        final Object object = new Object();

        new Thread(() -> {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + " --- 外层调用");
                synchronized (object) {
                    System.out.println(Thread.currentThread().getName() + " --- 中层调用");
                    synchronized (object) {
                        System.out.println(Thread.currentThread().getName() + " --- 内层调用");
                    }
                }
            }
        }, "t1").start();
    }

}
