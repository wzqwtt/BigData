package com.wzq.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 卖票，演示公平和非公平锁
 *
 * @author wzq
 * @create 2022-08-12 13:39
 */
// 资源类，模拟三个售票员卖完50张票
class Ticket {
    private int number = 50;
    ReentrantLock lock = new ReentrantLock(true);   // 公平锁

    public void sale() {
        try {
            lock.lock();    // 上锁
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖票，票号为：" + number);
                number--;
            }
        } finally {
            lock.unlock();  // 解锁
        }
    }
}

public class SaleTicketDemo {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i = 0; i < 55; i++) {
                ticket.sale();
            }
        }, "a").start();

        new Thread(() -> {
            for (int i = 0; i < 55; i++) {
                ticket.sale();
            }
        }, "b").start();

        new Thread(() -> {
            for (int i = 0; i < 55; i++) {
                ticket.sale();
            }
        }, "c").start();
    }

}
