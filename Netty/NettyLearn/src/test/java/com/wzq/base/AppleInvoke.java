package com.wzq.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author wzq
 * @create 2022-11-26 22:05
 */
public class AppleInvoke {

    public static void main(String[] args) throws Exception {

        // 正常的调用
        Apple apple = new Apple();
        apple.setPrice(5);
        System.out.println("Apple Price: " + apple.getPrice());

        // 使用反射调用
        // 获取类的Class对象实例
        Class clz = Class.forName("com.wzq.base.Apple");

        // 获取方法的Method对象
        Method setPriceMethod = clz.getMethod("setPrice", int.class);

        // 根据Class对象实例获取Constructor对象
        Constructor appleConstructor = clz.getConstructor();

        // 使用Constructor对象的netInstance方法获取反射类对象
        Object appleObj = appleConstructor.newInstance();

        // 利用invoke方法调用方法
        setPriceMethod.invoke(appleObj, 14);

        Method getPriceMethod = clz.getMethod("getPrice");
        System.out.println("Apple Price: " + getPriceMethod.invoke(appleObj));

    }

}
