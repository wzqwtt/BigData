package com.book.ch1;

/**
 * 以创建Runnable接口实例的方式创建线程
 *
 * @author wzq
 * @create 2022-09-07 15:56
 */
public class WelcomeApp1 {
    public static void main(String[] args) {
        Thread thread = new Thread(new WelcomeTask());
        thread.start();
        // thread.start(); // 多次调用抛出IllegalThreadStateException异常
        System.out.println("1. My name is " + Thread.currentThread().getName());
    }
}

class WelcomeTask implements Runnable {

    @Override
    public void run() {
        System.out.println("2. My name is " + Thread.currentThread().getName());
    }
}