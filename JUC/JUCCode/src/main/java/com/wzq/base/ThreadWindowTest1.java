package com.wzq.base;

/**
 * 例子：创建三个窗口卖票，总票数为100张，使用实现Runnable接口的方式
 * <p>
 * 1、问题：存在线程安全问题，在卖票的过程中，出现了重票、错票
 * 2、原因：当某个线程操作车票的过程中，尚未操作完成时，其他线程参与了进来，也操作车票
 * 3、如何解决：当一个线程A操作ticket的时候，其他线程不能参与进来。直到线程A操作完ticket时候，
 * 线程才可以开始操作ticket，这种情况下即使线程A出现了阻塞， 也不能被改变
 * 4、在Java中，通过同步机制，来解决线程安全问题
 * 方式一：同步代码块
 * <p>
 * synchronized (同步监视器) {
 * // 需要被同步的代码
 * }
 * <p>
 * 说明：1、操作共享数据的代码，即为需要被同步的代码
 * 2、共享数据：多个线程共同操作的变量。比如：ticket就是共享数据
 * 3、同步监视器，俗称：锁。任何一个类的对象，都可以充当锁
 * <b>要求：多个线程必须要共有一把锁</b>
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
            // synchronized (obj) {
            // 也可以使用本对象，使用this
            synchronized (this) {
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
