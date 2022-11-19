package com.wzq.netty.channel;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author wzq
 * @create 2022-11-19 13:34
 */
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 1、准备EventLoop对象
        EventLoop eventLoop = new DefaultEventLoopGroup().next();

        // 2、主动创建Promise容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        
        new Thread(() -> {
          // 3、任意一个线程执行完毕，计算完毕后向Promise填充结果
            log.debug("执行计算");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 往Promise里面填充结果
            promise.setSuccess(1);

//            // 也可以填充异常
//            try {
//                int i = 1 / 0;
//            } catch (Exception e) {
//                promise.setFailure(e);
//            }

        } ).start();


        // 4、接收结果的线程
        log.debug("等待结果");
        log.debug("结果是: {}",promise.sync().get());
    }

}
