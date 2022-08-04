package com.wzq.base;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 案例：银行有一个账户，有两个储户分别向同一个账户存3000元，每次存1000块，存3次。每次存完打印账户余额
 * <p>
 * 分析：
 * 1、是否有多线程问题？是，两个储户线程
 * 2、是否有共享数据？是，账户（账户余额）
 * 3、是否有线程安全问题？有
 * 4、需要考虑如何解决线程安全问题？同步机制：三种方式
 * <p>
 * 实现：使用lock锁
 *
 * @author wzq
 * @create 2022-08-04 23:44
 */
class Account2 {
    private double money;

    private static Account2 acct = null;
    private ReentrantLock lock = new ReentrantLock();

    private Account2() {
    }

    private Account2(double money) {
        this.money = money;
    }

    public static Account2 getInstance() {
        if (acct == null) {
            acct = new Account2(0);
        }
        return acct;
    }

    public void deposit(double amt) {
        try {
            // 上锁
            lock.lock();
            money += amt;
            // 模拟线程安全问题
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "存钱，余额：" + money);
        } finally {
            // 解锁
            lock.unlock();
        }
    }
}

class Customer2 implements Runnable {

    private Account2 acct = null;

    public Customer2(Account2 acct) {
        this.acct = acct;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            acct.deposit(1000);
        }
    }
}

public class AccountTest2 {
    public static void main(String[] args) {
        Account2 instance = Account2.getInstance();

        Customer2 c1 = new Customer2(instance);
        Customer2 c2 = new Customer2(instance);

        Thread t1 = new Thread(c1, "甲");
        Thread t2 = new Thread(c2, "乙");

        t1.start();
        t2.start();
    }
}
