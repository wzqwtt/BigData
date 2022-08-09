package com.wzq.base;

/**
 * 一般情况下，不做特别说明配置，默认都是用户线程。
 *
 * 如果用户线程全部结束意味着程序需要完成的业务操作已经结束了，守护线程随着JVM一同结束
 *
 * @author wzq
 * @create 2022-08-07 21:29
 */
public class DaemonDemo {

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " " +
                    (Thread.currentThread().isDaemon() ? "守护线程" : "用户线程"));
            while (true) {

            }
        }, "t1");

        t1.setDaemon(true); // 设置该线程为守护线程
        t1.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "--- end");

    }

}
