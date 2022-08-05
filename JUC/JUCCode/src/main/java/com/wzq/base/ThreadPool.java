package com.wzq.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池创建线程
 *
 * @author wzq
 * @create 2022-08-05 17:46
 */
class NumThread1 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
    }
}

class NumThread2 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
    }
}

public class ThreadPool {
    public static void main(String[] args) {
        // 1、提供指定线程数量的线程池
        ExecutorService service = Executors.newFixedThreadPool(10);

        // 2、执行指定的线程的操作，需要提供实现Runnable接口或Callable接口实现类的对象
        // 适合用于Runnable
        service.execute(new NumThread1());
        service.execute(new NumThread2());

        // service.submit(Callable callable); // 适用于Callable

        // 3、关闭连接池
        // 如果线程池不用了，可以关闭
        service.shutdown();
    }
}
