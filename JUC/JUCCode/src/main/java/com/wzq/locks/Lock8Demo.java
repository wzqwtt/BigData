package com.wzq.locks;

import java.util.concurrent.*;

/**
 * 题目：谈谈你对多线程锁的理解，8锁案例说明
 *
 * 1、标准访问有a、b两个线程，请问先打印邮件还是短信 -> 邮件、短信
 * 2、sendEmail方法中加入暂停3秒钟，请问先打印邮件还是短信 -> 邮件、短信
 * 3、添加一个普通的hello方法，请问先打印邮件还是hello -> hello、邮件
 * 4、有两部手机，请问先打印邮件还是短信 -> 短信、邮件
 * 5、有两个静态同步方法，有1部手机，请问先打印邮件还是短信 -> 邮件、短信
 * 6、有两个静态同步方法，有2部手机，请问先打印邮件还是短信 -> 邮件、短信
 * 7、有1个静态同步方法，有1个普通同步方法，有1部手机，请问先打印邮件还是短信 -> 短信、邮件
 * 8、有1个静态同步方法，有1个普通同步方法，有2部手机，请问先打印邮件还是短信 -> 短信、邮件
 *
 * <p>
 * 解析：
 * <p>
 * 1-2:
 *      一个对象里面如果有多个synchronized方法，某一个时刻内，只要一个线程去调用其中一个synchronized方法，
 *      其他线程都只能等待。换句话说，某一个时刻内，只能有唯一一个线程去访问这些synchronized方法，锁的是当前
 *      对象this，被锁定后，其他线程都不能进入到当前对象的其他synchronized方法
 * <p>
 * 3-4:
 *      加入普通方法后发现和同步锁无关
 *      换成两个对象后，不是同一把锁了，情况立刻变化
 * <p>
 * 5-6:
 *      都换成静态同步方法后，情况又有变化
 *      三种 synchronized 锁的内容有一些差别：
 *      对于普通同步方法，锁的是当前实例对象，通常指this，具体的一部手机，所有的普通同步方法用的都是同一把锁 -> 实例对象本身
 *      对于静态同步方法，锁的是当前类的Class对象，如Phone.class唯一的一个模板
 *      对于同步方法块，锁的是 synchronized 括号内的对象
 * <p>
 * 7-8:
 *      当一个线程试图访问同步代码时它首先必须得到锁，正常推出或抛出异常时必须释放锁
 *
 *      所有的普通同步方法用的都是同一把锁 -> 实例对象本身，就是new出来的具体实例对象本身，本类this
 *      也就是说如果一个实例对象的普通同步方法获取锁后，该实例对象的其他普通同步方法必须等待获取锁的方法释放锁后才能获取锁
 *
 *      所有静态同步方法用的也是同一把锁 -> 类对象本身，就是说过的唯一模板Class
 *      具体实例对象this和唯一模板Class，这两把锁是两个不同的对象，所以静态同步方法与普通同步方法之间是不会有竟态条件的
 *      但是一旦一个静态同步方法获取锁后，其他的静态同步方法就必须等待了
 *
 * @author wzq
 * @create 2022-08-12 15:53
 */
class Phone {

    public synchronized static void sendEmail() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("send Email....");
    }

    public synchronized void senSMS() {
        System.out.println("send SMS....");
    }

    public void sendHello() {
        System.out.println("Hello ....");
    }

}

public class Lock8Demo {

    public static void main(String[] args) {

        Phone phone1 = new Phone();
        Phone phone2 = new Phone();

        new Thread(() -> {
            phone1.sendEmail();
        } ,"a").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
//            phone1.senSMS();
//            phone1.sendHello();
            phone2.senSMS();
        } ,"b").start();


    }

}

