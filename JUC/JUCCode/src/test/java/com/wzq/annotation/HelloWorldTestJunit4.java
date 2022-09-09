package com.wzq.annotation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Junit4使用注解测试
 *
 * @author wzq
 * @create 2022-09-09 22:41
 */
public class HelloWorldTestJunit4 {

    private HelloWord hw;

    @Before
    public void setUp() {
        hw = new HelloWord();
    }

    // 1. 测试没有返回值，有别于Junit3的使用，更加方便
    @Test(expected = NumberFormatException.class)
    public void testHello() {
        hw.sayHello();
    }

    @Test
    public void testWorld() {
        hw.sayWorld();
    }


    // 2. 测试有返回值的方法
    // 返回字符串
    @Test
    public void testSay() {
        assertEquals("测试失败", hw.say(), "hello world!");
    }

    @Test
    public void testObj() {
        assertNull("测试对象不为空",null);
        assertNotNull("测试对象为空",new String());
    }

    @After
    public void tearDown() throws Exception {
        hw = null;
    }

}
