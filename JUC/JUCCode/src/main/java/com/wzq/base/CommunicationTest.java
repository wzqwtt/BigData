package com.wzq.base;

/**
 * 线程通信的例子：使用两个线程交替打印1-100
 * <p>
 * 涉及到的三个方法：
 * 1）wait()：一旦执行此方法，当前线程就会进入阻塞状态，并且释放锁
 * 2）notify()：一旦执行此方法，就会唤醒被wait的一个线程。如果多个线程被wait，就唤醒优先级最高的那个线程
 * 3）notifyAll()：一旦执行此方法，就会唤醒所有被wait的线程
 * <p>
 * 说明：
 * 1）这三个方法必须使用在同步代码块或同步方法中
 * 2）这三个方法的调用者必须是同步代码块或同步方法中的同步监视器，否则会出现：IllegalMonitorStateException 异常
 * 3）这三个方法是定义在java.lang.Object中
 *
 * @author wzq
 * @create 2022-08-05 15:30
 */
class Number implements Runnable {
    private int number = 1;

    @Override
    public void run() {
        while (true) {
            // 锁
            synchronized (this) {
                notify();   // 唤醒另一个线程
                if (number <= 100) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + ": " + number);
                    number++;

                    try {
                        wait(); // 该线程进入阻塞状态，并释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }
}

public class CommunicationTest {

    public static void main(String[] args) {
        Number number = new Number();

        Thread t1 = new Thread(number, "线程1");
        Thread t2 = new Thread(number, "线程2");

        t1.start();
        t2.start();
    }
}
