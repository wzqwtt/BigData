package com.wzq.base;

/**
 * 练习：创建两个分线程，其中一个线程遍历100以内的偶数，另一个线程遍历100以内的奇数
 *
 * @author wzq
 * @create 2022-08-02 22:14
 */
class Thread01 extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": 偶数" + i);
            }
        }
    }

}

class Thread02 extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ": 奇数" + i);
            }
        }
    }

}

public class ThreadExer01 {

    public static void main(String[] args) {
//        Thread01 t1 = new Thread01();
//        Thread02 t2 = new Thread02();
//
//        t1.start();
//        t2.start();

        // 匿名内部类
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 2 == 0) {
                        System.out.println(Thread.currentThread().getName() + ": 偶数" + i);
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 2 != 0) {
                        System.out.println(Thread.currentThread().getName() + ": 奇数" + i);
                    }
                }
            }
        }.start();
    }

}