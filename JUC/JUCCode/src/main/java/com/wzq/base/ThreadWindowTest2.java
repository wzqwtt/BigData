package com.wzq.base;

/**
 * 卖票案例
 * <p>
 * 使用同步方法解决实现Runnable接口方式的线程安全问题
 *
 * @author wzq
 * @create 2022-08-04 17:37
 */
class Window2 implements Runnable {

    private int ticket = 100;

    @Override
    public void run() {
        while (true) {
            if (ticket <= 0) {
                System.out.println("暂无余票！");
                break;
            }
            show();
        }
    }

    // 此刻同步监视器为this
    private synchronized void show() {
        if (ticket > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + "卖票，票号为：" + ticket);
            ticket--;
        }
    }
}

public class ThreadWindowTest2 {

    public static void main(String[] args) {
        Window2 w2 = new Window2();

        Thread t1 = new Thread(w2, "窗口1");
        Thread t2 = new Thread(w2, "窗口2");
        Thread t3 = new Thread(w2, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
