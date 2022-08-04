package com.wzq.base;

/**
 * 卖票案例
 * <p>
 * 使用基于继承Thread的方式
 *
 * @author wzq
 * @create 2022-08-04 18:02
 */
class Window3 extends Thread {

    private static int ticket = 100;

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

    // 同步方法，此刻同步监视器为 当前类对象 Window3.class
    private static synchronized void show() {
        if (ticket > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "卖票，票号：" + ticket);
            ticket--;
        }
    }

    // 构造方法，传递线程name
    public Window3(String name) {
        super(name);
    }

}

public class ThreadWindowTest3 {
    public static void main(String[] args) {
        Window3 t1 = new Window3("窗口1");
        Window3 t2 = new Window3("窗口2");
        Window3 t3 = new Window3("窗口3");

        t1.start();
        t2.start();
        t3.start();
    }
}
