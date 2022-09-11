package com.book.ch2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程终止与可见性
 * <p>
 * 一个线程终止后该线程对共享变量的更新对于调用线程的join方法的线程而言是可见的
 *
 * @author wzq
 * @create 2022-09-08 20:21
 */
public class ThreadJoinVisibility {

    // 线程之间的共享变量
    static int data = 0;

    public static void main(String[] args) {


        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 更新data的值
            data = 1;
        }, "t1");

        t1.start();

        // 等待线程t1结束后，main线程再继续执行
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 读取并打印共享变量data的值
        System.out.println(data);
    }

}
