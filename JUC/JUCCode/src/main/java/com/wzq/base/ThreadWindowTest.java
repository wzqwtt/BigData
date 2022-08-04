package com.wzq.base;

/**
 * 例子：创建三个窗口卖票，总票数为100张，使用继承Thread类的方式
 * <p>
 * 存在线程安全问题
 * <p>
 * 使用同步代码块解决继承Thread类的方式线程安全问题
 *
 * @author wzq
 * @create 2022-08-03 15:46
 */
class Window extends Thread {

    private static int ticket = 100;

    private static Object obj = new Object();

    @Override
    public void run() {
        while (true) {
            // synchronized (obj) {
            synchronized (Window.class) {
                if (ticket > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "卖票，票号为: " + ticket);
                    ticket--;
                } else {
                    System.out.println("已无多余的票");
                    break;
                }
            }
        }
    }

    // 构造方法：用于给线程起名字
    public Window(String name) {
        super(name);
    }

}

public class ThreadWindowTest {

    public static void main(String[] args) {
        Window w1 = new Window("窗口1");
        Window w2 = new Window("窗口2");
        Window w3 = new Window("窗口3");

        w1.start();
        w2.start();
        w3.start();
    }

}
