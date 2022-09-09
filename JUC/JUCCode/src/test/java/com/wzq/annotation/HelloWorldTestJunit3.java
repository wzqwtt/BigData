package com.wzq.annotation;

import junit.framework.TestCase;

/**
 * Junit3实现测试
 *
 * @author wzq
 * @create 2022-09-09 22:36
 */
public class HelloWorldTestJunit3 extends TestCase {

    private HelloWord hw;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        hw = new HelloWord();
    }

    // 1. 测试没有返回值
    public void testHello() {
        try {
            hw.sayHello();
        } catch (Exception e) {
            System.out.println("发生异常......");
        }
    }

    public void testWorld() {
        hw.sayWorld();
    }

    // 2. 测试有返回值的方法
    // 返回字符串
    public void testSay() {
        assertEquals("测试失败", hw.say(), "hello world!");
    }

    // 返回对象
    public void testObj() {
        assertNull("测试对象不为空", null);
        assertNotNull("测试对象为空", new String());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        hw = null;
    }
}
