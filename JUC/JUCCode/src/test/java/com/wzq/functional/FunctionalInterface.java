package com.wzq.functional;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * 函数式接口：https://www.cnblogs.com/dgwblog/p/11739500.html
 * <p>
 * 1、Supplier 你要作为一个供应者，自己生成数据
 * 2、Consumer 你要作为一个消费者，利用已经准备好的数据
 * 3、Function 输入一个或者两个不同或者相同的值转为另一个值
 * 4、Predicate 输入一个或者两个不同或者相同的值总是输出boolean
 * 5、UnaryOperator 输入一个值转换为相同值输出
 * 6、BinaryOperator 输入两个相同类型的值转为相同类型的值输出
 *
 * @author wzq
 * @create 2022-08-08 21:23
 */
public class FunctionalInterface {

    /**
     * 供应者接口，你要作为一个供应者，自己生成数据
     * Supplier<T> 接口仅包含一个T get()方法
     * 用来获取一个泛型参数指定类型的对象数据
     */
    @Test
    public void supplierTest1() {
        System.out.println(test_Supplier(() -> "产生数据"));

        System.out.println(String.valueOf(new Supplier<String>() {
            @Override
            public String get() {
                return "产生数据";
            }
        }));
    }

    @Test
    public void supplierTest2() {
        Integer[] data = {1, 2, 3, 4, 5};

        int res = getMax(() -> {
            int max = Integer.MIN_VALUE;
            for (Integer integer : data) {
                max = Math.max(integer, max);
            }
            return max;
        });

        System.out.println(res);
    }

    private static String test_Supplier(Supplier<String> suply) {
        return suply.get();
    }

    private static int getMax(Supplier<Integer> supplier) {
        return supplier.get();
    }

    /**
     * Consumer 你要作为一个消费者，利用已经准备好的数据
     * 它不是生产一个数据，而是消费一个数据，其数据类型由泛型决定
     * Consumer 接口中包含抽象方法 void accept(T t) ，意为消费一个指定泛型的数据。
     * 有一个默认方法：andThen 如果一个方法的参数和返回值全都是 Consumer 类型，那么就可以实现效果:
     * 消费数据的时候，首先做一个操作， 然后再做一个操作，实现组合。
     * 要想实现组合，需要两个或多个Lambda表达式即可，而 andThen 的语义正是“一步接一步”操作
     */
    @Test
    public void consumerTest1() {
        generateX(x -> System.out.println(x));
    }

    @Test
    public void consumerTest2() {
        String[] array = {"大雄，男", "静香，女", "胖虎，男"};

        printInfo(
                s -> {
                    System.out.println("姓名：" + s.split("，")[0] + "，");
                },
                s -> {
                    System.out.println("性别：" + s.split("，")[1]);
                },
                array
        );
    }

    private static void generateX(Consumer<Integer> consumer) {
        consumer.accept(ThreadLocalRandom.current().nextInt(10));
    }

    private static void printInfo(Consumer<String> one, Consumer<String> two, String[] array) {
        for (String s : array) {
            one.andThen(two).accept(s);
        }
    }

    /**
     * 有时候需要对某种类型的数据进行判断，从而得到一个boolean值的结果，这时候可以用Predicate接口
     * 抽象方法：test(T t) 对传入的参数进行验证，满足条件返回true，否则返回false
     * <ul>
     *     <li>predicateTest1方法：简单测试</li>
     *     <li>测试默认方法 and 与 </li>
     *     <li>测试默认方法 or 或 </li>
     * </ul>
     */
    @Test
    public void predicateTest1() {
        method1(s -> s.length() > 5, "Hello World");
    }

    private static void method1(Predicate<String> predicate, String s) {
        boolean test = predicate.test(s);
        System.out.println(test);
    }

    @Test
    public void predicateTest2() {
        // and：第一个条件，性别为男；第二个条件，名字中有大字
        // or：同上

        String[] array = {"大雄，男", "静香，女", "胖虎，男"};

        method2(
                s -> "男".equals(s.split("，")[1]),
                s -> s.split("，")[0].contains("大"),
                array
        );

    }

    private static void method2(Predicate<String> one, Predicate<String> two, String[] array) {
        for (String s : array) {
            boolean andTest = one.and(two).test(s);
            boolean orTest = one.or(two).test(s);
            System.out.println(s + " and:" + (andTest ? "满足条件" : "不满足条件")
                    + " or:" + (orTest ? "满足条件" : "不满足条件"));
        }
    }

    /**
     * java.util.function.Function<T,R> 接口用来根据一个类型的数据得到另一个类型的数据，前者称为前置条件，后者称为后置条件
     */
    @Test
    public void functionTest() {
        numberToString(s -> String.valueOf(s), 12);
    }

    private static void numberToString(Function<Number, String> function, int data) {
        String res = function.apply(data);
        System.out.println("转换结果：" + res);
    }

}
