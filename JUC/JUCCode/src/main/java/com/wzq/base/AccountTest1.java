package com.wzq.base;

/**
 * 案例：银行有一个账户，有两个储户分别向同一个账户存3000元，每次存1000块，存3次。每次存完打印账户余额
 * <p>
 * 分析：
 * 1、是否有多线程问题？是，两个储户线程
 * 2、是否有共享数据？是，账户（账户余额）
 * 3、是否有线程安全问题？有
 * 4、需要考虑如何解决线程安全问题？同步机制：三种方式
 * <p>
 * 实现：基于继承Thread类的方式
 *
 * @author wzq
 * @create 2022-08-04 23:31
 */
// 单例模式Account1
class Account1 {
    private double money = 0;

    private static Account1 instance = null;

    private Account1() {
    }

    public synchronized static Account1 getInstance() {
        if (instance == null) {
            instance = new Account1();
        }
        return instance;
    }

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

class Customer1 extends Thread {

    private Account1 acct = null;

    public Customer1(Account1 acct) {
        this.acct = acct;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            acct.deposit(1000);
        }
    }
}

public class AccountTest1 {

    public static void main(String[] args) {
        Account1 acct = Account1.getInstance();

        Customer1 c1 = new Customer1(acct);
        Customer1 c2 = new Customer1(acct);

        c1.setName("甲");
        c2.setName("乙");

        c1.start();
        c2.start();
    }

}
