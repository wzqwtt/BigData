package com.wzq.lockSupports;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport实现线程的等待和唤醒，可以解决Object和Lock的痛点
 * <p>
 * m1方法：正常使用
 * <p>
 * m2方法：提前唤醒线程，没有报错
 *
 * @author wzq
 * @create 2022-10-23 20:26
 */
public class LockSupportParkAndUnpark {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            // t1线程先休眠1秒钟
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 打印被阻塞前的时间
            System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis());
            LockSupport.park(); // 这里拿到许可证，就没有许可证了
            System.out.println("--------");
            LockSupport.park(); // 已经没有许可证了，所以他会一直等待
            System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis() + "\t被唤醒");
        }, "t1");
        t1.start();

        // t2线程唤醒t1线程
        new Thread(() -> {
            // 许可证不会累计，即使发4个，也只有1个
            LockSupport.unpark(t1);
            LockSupport.unpark(t1);
            LockSupport.unpark(t1);
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName() + "\t----唤醒线程");
        }, "t2").start();
    }

    private static void m2() {
        Thread t1 = new Thread(() -> {
            // t1线程先休眠1秒钟
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 打印被阻塞前的时间
            System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis());
            LockSupport.park(); // 线程等待
            System.out.println(Thread.currentThread().getName() + "\t" + System.currentTimeMillis() + "\t被唤醒");
        }, "t1");
        t1.start();

        // t2线程唤醒t1线程
        new Thread(() -> {
            LockSupport.unpark(t1); // 唤醒t1线程
            System.out.println(Thread.currentThread().getName() + "\t----唤醒线程");
        }, "t2").start();
    }

    private static void m1() {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t----come in");
            // 使用park，等待其他线程发许可证
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
        }, "t1");
        t1.start();

        // 暂停几秒线程（main）
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName() + "\t----唤醒线程");
        }, "t2").start();
    }

}
