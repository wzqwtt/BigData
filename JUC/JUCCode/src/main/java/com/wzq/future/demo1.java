package com.wzq.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author wzq
 * @create 2022-08-08 0:01
 */
public class demo1 {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        FutureTask<String> task1 = new FutureTask<String>(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace();}
            return "task1";
        });
        threadPool.submit(task1);

        FutureTask<String> task2 = new FutureTask<>(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
            return "task2";
        });
        threadPool.submit(task2);

        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();}

        try {
            System.out.println(task1.get());
            System.out.println(task2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime - startTime) + " 毫秒");
        threadPool.shutdown();      // 释放资源

    }

    private static void method2() {
        long startTime = System.currentTimeMillis();

        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace();}
        } ,"t1").start();

        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();}
        } ,"t2").start();

        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();}

        long endTime = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime - startTime) + " 毫秒");
    }

    private static void method1() {
        long startTime = System.currentTimeMillis();

        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace();}
        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();}
        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace();}

        long endTime = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime - startTime) + " 毫秒");
    }

}
