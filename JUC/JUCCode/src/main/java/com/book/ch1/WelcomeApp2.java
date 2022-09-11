package com.book.ch1;

/**
 * 应用代码直接调用线程的run方法（避免这样做！）
 *
 * @author wzq
 * @create 2022-09-07 16:05
 */
public class WelcomeApp2 {

    public static void main(String[] args) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("2. My name is " + Thread.currentThread().getName());
            }
        });

        // 启动线程
        thread.start();
        // 直接调用线程的run方法，避免这样做
        thread.run();   // 此时线程在main线程
        System.out.println("1. My name is " + Thread.currentThread().getName());
    }

}
