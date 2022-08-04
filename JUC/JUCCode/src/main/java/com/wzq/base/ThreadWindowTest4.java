package com.wzq.base;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 案例：卖票，使用lock锁
 *
 * @author wzq
 * @create 2022-08-04 21:18
 */
class Window4 implements Runnable {

    private int ticket = 100;
    // 1、实例化对象
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        while (true) {
            try {
                // 2、加锁
                lock.lock();
                if (ticket > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "卖票，票号为：" + ticket);
                    ticket--;
                } else {
                    System.out.println("暂无余票！");
                    break;
                }
            } finally {
                // 3、解锁
                lock.unlock();
            }
        }
    }
}

public class ThreadWindowTest4 {

    public static void main(String[] args) {
        Window4 w = new Window4();

        Thread t1 = new Thread(w, "窗口1");
        Thread t2 = new Thread(w, "窗口2");
        Thread t3 = new Thread(w, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
