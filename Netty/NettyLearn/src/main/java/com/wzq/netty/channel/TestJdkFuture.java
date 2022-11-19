package com.wzq.netty.channel;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 使用JDK Future
 *
 * @author wzq
 * @create 2022-11-19 13:23
 */
@Slf4j
public class TestJdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1、线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        // 2、提交任务
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        // 3、等待获取结果
        log.debug("等待结果");
        while (true) {
            if (future.isDone()) {
                log.debug("结果是：{}", future.get());
                service.shutdown();
                break;
            }
        }
    }
}
