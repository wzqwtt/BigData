package com.book.ch1;

/**
 * 定义Thread子类的方式创建线程
 *
 * @author wzq
 * @create 2022-09-07 15:53
 */
public class WelcomeApp {
    public static void main(String[] args) {
        Thread t1 = new WelcomeThread();
        t1.setName("t1");   // 设置线程名称
        t1.start();
        System.out.println("线程名称：" + Thread.currentThread().getName());
    }
}

class WelcomeThread extends Thread {
    @Override
    public void run() {
        System.out.println("--------welcome--------");
        System.out.println("线程名称：" + Thread.currentThread().getName());
    }
}
