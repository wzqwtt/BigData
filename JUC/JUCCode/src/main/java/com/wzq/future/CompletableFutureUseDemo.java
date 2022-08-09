package com.wzq.future;

import java.util.concurrent.*;

/**
 * @author wzq
 * @create 2022-08-08 21:08
 */
public class CompletableFutureUseDemo {

    public static void main(String[] args) {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + " ----come in");
                int res = ThreadLocalRandom.current().nextInt(10);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("等待1秒出结果");
                if (res < 2) {
                    int a = 1 / 0;  // 模拟异常
                }
                return res;
            },threadPool).whenComplete((v, e) -> {
                if (e == null) {
                    System.out.println("任务完成，更新值为：" + v);
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println("出现异常！" + e.getCause() + "\t" + e.getMessage());
                return null;
            });

            System.out.println(Thread.currentThread().getName() + "去忙其他任务了！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private static void method2() throws InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " ----come in");
            int res = ThreadLocalRandom.current().nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("等待1秒钟出结果");
            return res;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("----计算完成，更新值：" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            System.out.println("异常情况：" + e.getCause() + "\t" + e.getMessage());
            return null;
        });

        System.out.println(Thread.currentThread().getName() + "先去忙其他任务了！");

        // 没有使用自己的线程池，用的ForkJoinPool，所以是守护线程，会自动关系，这里先睡3秒钟
        TimeUnit.SECONDS.sleep(3);
    }

    private static void method1() throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " ----come in");
            int tmp = ThreadLocalRandom.current().nextInt(10);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("-----1秒后出结果：" + tmp);
            return tmp;
        });

        System.out.println(Thread.currentThread().getName() + "先去忙其他任务了！");

        System.out.println(completableFuture.get());    // 会导致线程阻塞
    }

}
