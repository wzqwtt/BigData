package com.wzq.patterns.proxy;

/**
 * @author wzq
 * @create 2022-11-28 20:14
 */
public class ProxyPatternDemo {

    public static void main(String[] args) {
        Image image = new ProxyImage("test.jpg");

        // 图像从磁盘加载
        image.display();
        System.out.println("");

        // 第二次再调用，图像不需要从磁盘加载
        image.display();
    }

}
