package com.wzq.io;

import java.io.Serializable;

/**
 * Person类需要满足如下的要求，方可序列化：
 * <p>
 * 1、需要实现Serializable接口（标识接口）
 * 2、当前类提供一个全局常量：serialVersionUID
 * 3、除了当前Person类需要实现的Serializable接口之外，<b>还必须保证其内部的所有属性都是可序列化的</b>
 * （默认情况下，基本数据类型都是可序列化的）
 * <p>
 * 注意：ObjectOutputStream和ObjectInputStream不能序列化static和transient修饰的成员变量
 *
 * @author wzq
 * @create 2022-08-18 0:12
 */
public class Person implements Serializable {

    // 全局唯一标识
    public static final long serialVersionUID = 4154545451512L;

    private String name;
    private int age;
    private Account acct;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, Account acct) {
        this.name = name;
        this.age = age;
        this.acct = acct;
    }

    public Person() {
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", acct=" + acct +
                '}';
    }

    public Account getAcct() {
        return acct;
    }

    public void setAcct(Account acct) {
        this.acct = acct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
