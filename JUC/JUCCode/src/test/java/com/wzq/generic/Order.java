package com.wzq.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义泛型类
 *
 * @author wzq
 * @create 2022-08-05 22:44
 */
public class Order<T> {
    String orderName;
    int orderId;

    // 类的内部结构可以实验类的泛型
    T orderT;

    public Order() {
    }

    public Order(String orderName, int orderId, T orderT) {
        this.orderId = orderId;
        this.orderName = orderName;
        this.orderT = orderT;
    }

    // 以下的方法不是泛型方法
    public T getOrderT() {
        return orderT;
    }

    public void setOrderT(T orderT) {
        this.orderT = orderT;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderName='" + orderName + '\'' +
                ", orderId=" + orderId +
                ", orderT=" + orderT +
                '}';
    }

    // 泛型方法：在方法中出现了泛型结构，泛型参数与类的泛型参数没有任何关系
    // 泛型方法所属的类是不是泛型类都没关系
    // 泛型方法，可以声明为静态的。原因：泛型参数是在调用方法时确定的，并非在实例化类时确定
    public static  <E> List<E> copyFromArrayToList(E[] arr) {
        ArrayList<E> list = new ArrayList<>();
        for (E e : arr) {
            list.add(e);
        }
        return list;
    }
}
