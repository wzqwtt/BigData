package com.wzq.lockSupports;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock Condition Demo
 *
 * @author wzq
 * @create 2022-10-23 19:49
 */
public class ConditionDemo {

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    public static void main(String[] args) {

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "===> 进入等待");
                // 设置当前线程等待
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + "===> 继续执行");
        }, "t1").start();


        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "===>进入");
                Thread.sleep(2000); // 休眠两秒
                // 随机唤醒等待队列中的一个线程
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "休息结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }

}
