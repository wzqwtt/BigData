package com.wzq.annotation;

/**
 * 被测试的类
 *
 * @author wzq
 * @create 2022-09-09 22:35
 */
public class HelloWord {

    public void sayHello() {
        System.out.println("hello....");
        throw new NumberFormatException();
    }

    public void sayWorld() {
        System.out.println("world....");
    }

    public String say() {
        return "hello world";
    }

}
