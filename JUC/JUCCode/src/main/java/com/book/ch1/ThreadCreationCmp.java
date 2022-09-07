package com.book.ch1;

import java.util.concurrent.TimeUnit;

/**
 * 线程的两种创建方式对比
 *
 * @author wzq
 * @create 2022-09-07 16:11
 */
public class ThreadCreationCmp {

    public static void main(String[] args) {
        Thread t;
        CountingTask ct = new CountingTask();

        // 获取处理器个数
        final int numberOfProcessors = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < 2 * numberOfProcessors; i++) {
            // 直接创建线程
            t = new Thread(ct);
            t.start();
        }

        for (int i = 0; i < 2 * numberOfProcessors; i++) {
            // 以子类的方式创建
            t = new CountingThread();
            t.start();
        }
    }

    static class Counter {
        private int count = 0;

        public void increment() {
            this.count++;
        }

        public int value() {
            return this.count;
        }
    }

    static class CountingTask implements Runnable {
        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                doSomthing();
                counter.increment();
            }
            System.out.println("CountingTask : " + counter.value());
        }

        private void doSomthing() {
            // 使当前线程休眠100毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class CountingThread extends Thread {
        private Counter counter = new Counter();

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                doSomthing();
                counter.increment();
            }
            System.out.println("CountThread : " + counter.value());
        }

        private void doSomthing() {
            // 使当前线程休眠100毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
