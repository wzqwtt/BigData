package com.wzq.future;

import java.util.concurrent.*;

/**
 * @author wzq
 * @create 2022-08-08 20:23
 */
public class CompletableFutureBuildDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 不推荐使用new的方式
        // CompletableFuture completableFuture = new CompletableFuture();

//        method1();

//        method2();

//        method3();

        method4();

    }

    /**
     * supplyAsync静态方法，传递一个supplier函数式接口和一个线程池
     * 有返回值，需要指定泛型（自己指定）
     */
    private static void method4() throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " ---come in");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Pool Hello Async!";
        }, threadPool);

        System.out.println("返回值：" + completableFuture.get());

        threadPool.shutdown();
    }

    /**
     * supplyAsync静态方法，传递一个supplier函数式接口
     * 有返回值，需要指定泛型（自己指定）
     * 使用ForkJoinPool.commonPool线程池
     */
    private static void method3() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " ---come in");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello supplyAsync!";
        });

        System.out.println("返回值：" + completableFuture.get());
    }

    /**
     * runAsync方法运行线程，传递Runnable和Executor两个参数
     * 没有返回值，使用自己的线程池
     */
    private static void method2() throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " ---come in");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPool);

        System.out.println("返回值：" + completableFuture.get());

        // 释放线程池资源
        threadPool.shutdown();
    }

    /**
     * runAsync静态方法运行线程，传递Runnable接口；如果没有传递Exturetor线程池，则使用ForkJoinPool.commonPool线程池
     * 此方法没有返回值
     */
    private static void method1() throws InterruptedException, ExecutionException {
        // 默认ForkJoinPool.commonPool线程池
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " ---come in");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 需要get后才会去执行
        System.out.println("返回值：" + completableFuture.get());
    }

}
