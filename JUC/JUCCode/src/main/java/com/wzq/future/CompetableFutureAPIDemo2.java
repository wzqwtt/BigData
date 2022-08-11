package com.wzq.future;

import java.util.concurrent.*;

/**
 * @author wzq
 * @create 2022-08-10 21:47
 */
public class CompetableFutureAPIDemo2 {

    public static void main(String[] args) {
        handleTest();

        thenApplyTest2();
    }

    private static void handleTest() {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "---come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("第一步");
            return 1;
        }, threadPool).handle((v, e) -> {
            if (ThreadLocalRandom.current().nextInt() > 2) {
                int i = 1 / 0;
            }
            System.out.println("第二步");
            return v + 2;
        }).handle((v, e) -> {
            System.out.println("第三步");
            return v + 3;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("计算完成！结果为：" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            System.out.println("出现异常！" + e.getCause() + "\t" + e.getMessage());
            return null;
        });

        System.out.println(Thread.currentThread().getName() + "先去忙其他任务！");

        threadPool.shutdown();
    }

    private static void thenApplyTest2() {
        // 自定义线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "-----come in");
                // 线程暂停1秒钟
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("第一步");

                if (ThreadLocalRandom.current().nextInt(10) > 2) {
                    int a = 1 / 0;    // 模拟异常
                }

                return 1;
            }, threadPool).thenApply(f -> {
                System.out.println("第二步");
                return f + 2;
            }).thenApply(f -> {
                System.out.println("第三步");
                return f + 3;
            }).whenComplete((v, e) -> {
                if (e == null) {
                    System.out.println("计算完成！计算结果为：" + v);
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println("出现异常！" + e.getCause() + "\t" + e.getMessage());
                return null;
            });

            System.out.println(Thread.currentThread().getName() + "先去忙其他任务了！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private static void test1() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("1111");
                return 1;
            }, threadPool).thenApply(f -> {
                System.out.println("2222");
                return f + 2;
            }).thenApply(f -> {
                System.out.println("3333");
                return f + 3;
            }).whenComplete((v, e) -> {
                if (e == null) {
                    System.out.println("计算完成！计算结果为：" + v);
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println("出现异常：" + e.getCause() + " " + e.getMessage());
                return null;
            });


            System.out.println(Thread.currentThread().getName() + "----先去忙其他任务");
        } finally {
            threadPool.shutdown();
        }
    }

}
