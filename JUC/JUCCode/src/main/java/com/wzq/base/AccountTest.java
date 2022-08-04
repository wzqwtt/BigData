package com.wzq.base;

/**
 * 案例：银行有一个账户，有两个储户分别向同一个账户存3000元，每次存1000块，存3次。每次存完打印账户余额
 * <p>
 * 分析：
 * 1、是否有多线程问题？是，两个储户线程
 * 2、是否有共享数据？是，账户（账户余额）
 * 3、是否有线程安全问题？有
 * 4、需要考虑如何解决线程安全问题？同步机制：三种方式
 *
 * 实现：基于实现Runnable类的方式
 * @author wzq
 * @create 2022-08-04 21:46
 */
class Account {
    private double money = 0;

    public Account(double money) {
        this.money = money;
    }

    // 加锁，锁为this
    public synchronized void deposit(double amt) {
        money += amt;
        // 模拟线程安全问题
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "存钱，余额：" + money);
    }
}

class Customer implements Runnable {

    private Account account = null;

    public Customer(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            account.deposit(1000);
        }
    }
}

public class AccountTest {

    public static void main(String[] args) {
        Account account = new Account(0);

        Customer c1 = new Customer(account);
        Customer c2 = new Customer(account);

        Thread t1 = new Thread(c1, "甲");
        Thread t2 = new Thread(c2, "乙");

        t1.start();
        t2.start();
    }

}
