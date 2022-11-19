package com.wzq.netty.channel;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 测试Netty Future
 *
 * @author wzq
 * @create 2022-11-19 13:27
 */
@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 1、获取Netty的EventLoopGroup，相当于一个线程组
        DefaultEventLoopGroup group = new DefaultEventLoopGroup();

        // 2、获取一个EventLoop，相当于一个线程
        EventLoop eventLoop = group.next();

        // 3、提交任务，此时的future属于 io.netty.util.concurrent;
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
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

        log.debug("等待结果");
        // sync阻塞等待结果
//        log.debug("结果是：{}", future.sync().get());
//        group.shutdownGracefully();

        // addListener异步回调
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> listenerFuture) throws Exception {
                log.debug("结果是：{}", future.get());
                group.shutdownGracefully();
            }
        });
    }

}