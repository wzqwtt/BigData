package com.wzq.io;

import java.io.Serializable;

/**
 * 该类也必须可序列化
 *
 * @author wzq
 * @create 2022-08-18 0:27
 */
public class Account implements Serializable {

    public static final long serialVersionUID = 42484515151618478L;

    private double balance;

    public Account(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "balance=" + balance +
                '}';
    }
}
