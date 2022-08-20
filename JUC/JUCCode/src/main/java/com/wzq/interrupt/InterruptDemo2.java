package com.wzq.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * @author wzq
 * @create 2022-08-20 20:31
 */
public class InterruptDemo2 {

    public static void main(String[] args) {
        // 实例方法interrupt() 仅仅是设置中断标识位为true，并不能立刻停止线程
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 300; i++) {
                System.out.println("i = " + i);
            }
            System.out.println("t1线程内部执行完成后 标识位值：" + Thread.currentThread().isInterrupted());   // true
        }, "t1");
        t1.start();

        System.out.println("t1线程默认的中断标识位值：" + t1.isInterrupted());  // false

        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.interrupt(); // t1线程中断
        System.out.println("t1线程调用interrupt()方法后标识位值：" + t1.isInterrupted());   // true


        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // t1线程完全结束后，中断标识位不会受任何影响
        System.out.println("t1线程调用interrupt()方法后标识位值：" + t1.isInterrupted());   // false

    }

}
