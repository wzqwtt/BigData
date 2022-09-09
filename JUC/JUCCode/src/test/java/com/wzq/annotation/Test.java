package com.wzq.annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Java内置注解
 *
 * @author wzq
 * @create 2022-09-09 21:55
 */
public class Test {
    public static void main(String[] args) {
        B b = new B();
        // 被启用的方法
        b.oldMethod();
    }

}

class A {
    public void test() {

    }
}

@TestDocAnnotation("myMethodDoc")
class B extends A {
    /**
     * 重写父类的test方法
     */
    @Override
    public void test() {

    }

    /**
     * 被弃用的方法
     */
    @Deprecated
    public void oldMethod() {

    }

    /**
     * 忽略警告
     */
    @SuppressWarnings("rawtypes")
    public List processList() {
        List list = new ArrayList();
        return list;
    }

}