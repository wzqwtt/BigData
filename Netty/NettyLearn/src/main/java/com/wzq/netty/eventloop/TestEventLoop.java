package com.wzq.netty.eventloop;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.awt.datatransfer.ClipboardOwner;
import java.util.concurrent.TimeUnit;

/**
 * EventLoop方法
 *
 * @author wzq
 * @create 2022-11-16 19:41
 */
@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        // 创建Nio事件循环组，并指定使用2个线程
        EventLoopGroup group = new NioEventLoopGroup(2);

        // 输出自己电脑有多少个处理线程
        System.out.println(NettyRuntime.availableProcessors());

        // 获取下一个事件循环对象，多打印几个
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 执行普通任务
        group.next().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("ok");    // 在线程内打印
        });
        log.debug("main");  // 在外面打印

        // 执行异步定时任务
        group.next().schedule(() -> {
            log.debug("执行定时任务");
        }, 5, TimeUnit.SECONDS);

        // 执行异步周期定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.debug("周期性任务");
        }, 0, 1, TimeUnit.SECONDS);

    }

}
