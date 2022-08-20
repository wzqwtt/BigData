package com.wzq.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * @author wzq
 * @create 2022-08-20 20:48
 */
public class InterruptDemo3 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("t1线程 中断标识位：" + Thread.currentThread().isInterrupted() + " 程序停止");
                    break;
                }

                // 如果线程里面有sleep wait方法，那么将会抛出下面异常，并且线程不会停止
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();     // 再调用一次Interrupt，线程停止
                    e.printStackTrace();
                }

                System.out.println("hello t1!");
            }
        }, "t1");
        t1.start();

        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            t1.interrupt();
        }, "t2").start();

    }
}
