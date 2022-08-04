package com.wzq.base;

/**
 * 单例模式的线程安全
 *
 * @author wzq
 * @create 2022-08-04 18:26
 */
class Singleton {
    // 构造器私有化
    private Singleton() {
    }

    private static Singleton instance = null;

    // 锁为Singleton.class
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

public class ThreadSingleton {
    public static void main(String[] args) {

    }
}
