package com.wzq.base;

/**
 * 例子：创建三个窗口卖票，总票数为100张，使用实现Runnable接口的方式
 * <p>
 * 同样存在线程安全问题
 *
 * @author wzq
 * @create 2022-08-03 16:26
 */
class Window1 implements Runnable {

    private int ticket = 100;
    Object obj = new Object();  // 锁

    @Override
    public void run() {
        while (true) {
            // 同步代码块
            synchronized (obj) {
                if (ticket > 0) {
                    // 线程睡眠100毫秒，模拟线程安全问题
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + "卖票，票号为" + ticket);
                    ticket--;
                } else {
                    System.out.println("暂无余票！");
                    break;
                }
            }
        }
    }
}

public class ThreadWindowTest1 {

    public static void main(String[] args) {
        Window1 window = new Window1();
        Thread t1 = new Thread(window, "窗口1");
        Thread t2 = new Thread(window, "窗口2");
        Thread t3 = new Thread(window, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
