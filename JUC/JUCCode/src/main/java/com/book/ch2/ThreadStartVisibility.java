package com.book.ch2;

import java.util.concurrent.TimeUnit;

/**
 * 线程的启动与可见性
 * <p>
 * 父线程在启动子线程之前对共享变量的更新对于子线程而言是可见的
 *
 * @author wzq
 * @create 2022-09-08 20:17
 */
public class ThreadStartVisibility {

    // 线程间的共享变量
    static int data = 0;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            // 线程休眠50毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 读取并打印变量data的值
            System.out.println(data);
        }, "t1");

        // 在子线程被调用之前更改data的值
        data = 1;
        t1.start();

        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 在子线程thread启动后更新变量data的值
        data = 2;
    }

}
