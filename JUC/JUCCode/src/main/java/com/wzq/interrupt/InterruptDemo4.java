package com.wzq.interrupt;

/**
 * 静态方法interrupted()
 *
 * @author wzq
 * @create 2022-08-20 21:20
 */
public class InterruptDemo4 {

    public static void main(String[] args) {
        // 测试当前线程是否被中断（检查中断标志），返回一个boolean并清除中断状态
        // 第二次再调用时中断状态已经被清除，将返回一个false

        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted()); // false
        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted()); // false

        System.out.println("----1");
        Thread.currentThread().interrupt(); // 中断标识位设置为true
        System.out.println("----2");

        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted()); // true
        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted()); // false
    }

}
