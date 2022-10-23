package com.wzq.lockSupports;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示Condition接口中的await和signal方法实现线程的等待和唤醒
 * <p>
 * m1方法：正常使用Condition
 * <p>
 * m2方法：不在lock、unlock对里面使用Condition，直接报错
 * <p>
 * m3方法：先唤醒，再等待，程序永远不会停止
 *
 * @author wzq
 * @create 2022-10-23 19:55
 */
public class ConditionAwaitAndSignal {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            // 先睡一秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 上锁
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t----come in");
                // 使线程等待
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                // 唤醒线程
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "\t----唤醒线程");
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

    private static void m2() {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        // t1 线程负责等待
        new Thread(() -> {
//            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t----come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
//                lock.unlock();
            }
        }, "t1").start();

        // 暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // t2线程，负责唤醒t1线程
        new Thread(() -> {
//            lock.lock();
            try {
                condition.signal(); // 唤醒其他线程
                System.out.println(Thread.currentThread().getName() + "\t----唤醒其他线程");
            } finally {
//                lock.unlock();
            }
        }, "t2").start();
    }

    private static void m1() {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        // t1 线程负责等待
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t----come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t----被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        // 暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // t2线程，负责唤醒t1线程
        new Thread(() -> {
            lock.lock();
            try {
                condition.signal(); // 唤醒其他线程
                System.out.println(Thread.currentThread().getName() + "\t----唤醒其他线程");
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

}
