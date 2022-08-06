package com.wzq.generic;

import org.junit.Test;

import java.util.*;

/**
 * 泛型的使用
 * 1、jdk 5.0 新增的特性
 * 2、在集合中使用泛型
 * 1）集合接口或集合类都在jdk5.0时都修改为带泛型的结构
 * 2）在实例化集合类时，可以指明具体的泛型类型
 * 3）指明完以后，在集合类或接口中凡是定义类或接口时，内部结构在实例化之后都会变成类
 * 比如：add(E e) ---> 实例化以后 add(Integer e)
 * 4）泛型的类型必须是类，不能是基本数据类型。需要用到基本数据类型的位置，拿包装类替换
 * 5）如果实例化时，没有指明泛型的类型，默认泛型类型为Object
 *
 * @author wzq
 * @create 2022-08-05 21:09
 */
public class GenericTest {

    // 在集合中使用泛型之前的情况
    @Test
    public void test1() {
        // list存放学生成绩
        ArrayList list = new ArrayList();

        list.add(78);
        list.add(42);
        list.add(71);
        list.add(99);

        // 问题1：类型不安全
        list.add("Tom");

        for (Object score : list) {
            // 问题2：强转时，可能出现ClassCastException
            int stuScore = (int) score;
            System.out.println(stuScore);
        }
    }

    // 在集合中使用泛型的情况
    @Test
    public void test2() {
        ArrayList<Integer> list = new ArrayList<>();

        list.add(75);
        list.add(73);
        list.add(72);
        list.add(71);

        // 编译时就会进行类型检查，保证类型的安全
        // list.add("aaa");

        for (Integer score : list) {
            // 避免了强转操作
            int stuScore = score;
            System.out.println(stuScore);
        }
    }

    // 在集合中使用泛型的情况，以Map为例
    @Test
    public void test3() {
        Map<String, Integer> map = new HashMap<>();

        map.put("wzq", 11);
        map.put("wzqwtt", 13);
        map.put("wtt", 12);

        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        Iterator<Map.Entry<String, Integer>> iter = entries.iterator();

        while (iter.hasNext()) {
            Map.Entry<String, Integer> next = iter.next();
            Integer value = next.getValue();
            String key = next.getKey();
            System.out.println(key + "---" + value);
        }
    }

    @Test
    public void test4() {
        Order<String> order1 = new Order<>("orderAA", 1001, "order:AA");
        order1.setOrderT("order:BB");
        System.out.println(order1);

        // 由于子类在继承带泛型的父类时，指明了泛型类型，则实例化子类对象时，不再需要指明泛型
        SubOrder subOrder1 = new SubOrder();
        subOrder1.setOrderT(123);
        System.out.println(subOrder1);

        SubOrder1<String> subOrder2 = new SubOrder1<>();
        subOrder2.setOrderT("SubOrder2");
        System.out.println(subOrder2);
    }

    // 测试泛型方法
    @Test
    public void test5() {
        Order<String> order = new Order<>();
        Integer[] arr = {1, 2, 3, 4};
        // 泛型方法调用时，指明泛型参数的类型
        List<Integer> list = order.copyFromArrayToList(arr);
        System.out.println(list);
    }

    @Test
    public void test6() {
        List<String> list1 = null;
        List<Object> list2 = null;

        List<?> list = null;

        list = list1;
        list = list2;
    }

    public void print(List<?> list) {
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            System.out.println(next);
        }
    }
}
