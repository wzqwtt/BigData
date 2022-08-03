package com.wzq.base;

/**
 * 例子：创建三个窗口卖票，总票数为100张
 * <p>
 * 存在线程安全问题，待解决
 *
 * @author wzq
 * @create 2022-08-03 15:46
 */
class Window extends Thread {

    private static int ticket = 100;

    @Override
    public void run() {
        while (true) {
            if (ticket > 0) {
                System.out.println(Thread.currentThread().getName() + "卖票，票号为: " + ticket);
                ticket--;
            } else {
                System.out.println("已无多余的票");
                break;
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
