package com.book.ch3;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示线程转储显示锁信息的示例程序
 *
 * @author wzq
 * @create 2022-09-08 22:26
 */
public class ExplicitLockInfo {

    private static final Lock lock = new ReentrantLock();
    private static int sharedData = 0;

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();    // 上锁
                try {
                    try {
                        TimeUnit.SECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sharedData = 1;
                } finally {
                    lock.unlock();  // 解锁
                }
            }
        });

        thread.start();

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lock.lock();
        try {
            System.out.println("sharedData: " + sharedData);
        } finally {
            lock.unlock();
        }

    }

}
