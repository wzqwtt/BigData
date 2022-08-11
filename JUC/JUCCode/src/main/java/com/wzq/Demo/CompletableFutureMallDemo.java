package com.wzq.Demo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 案例说明：电商比价续期，模拟如下情况：
 * <p>
 * 1、需求
 * 1.1 同一款产品，同时搜索出同款产品在各大电商平台的售价
 * 1.2 同一款产品，同时搜索出本产品在同一个电商平台下，各个入驻卖家的售价
 * <p>
 * 2、输出：出来结果希望是同款产品在不同地方的价格清单列表，返回一个List<String>
 * 《mysql》 in jd price is 88.05
 * 《mysql》 in dangdang price is 86.11
 * 《mysql》 in taobao price is 90.43
 * <p>
 * 3、技术要求
 * 3.1 函数式编程
 * 3.2 链式编程
 * 3.3 Stream流式计算
 *
 * @author wzq
 * @create 2022-08-09 22:07
 */
public class CompletableFutureMallDemo {

    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("taobao"),
            new NetMall("pdd")
    );

    /**
     * step by step 一个接一个查
     *
     * @param list        电商网站list
     * @param productName 产品名字
     * @return 返回符合结果的数据
     */
    public static List<String> getPrice(List<NetMall> list, String productName) {
        // 《mysql》 in taobao price is 90.43
        return list.stream()
                .map(netMall ->
                        String.format("《" + productName + "》 in %s price is %.2f",
                                netMall.getNetMallName(),
                                netMall.calcPrice(productName)))
                .collect(Collectors.toList());
    }

    public static List<String> getPriceByCompletableFuture(List<NetMall> list, String productName) {
        return list
                .stream()
                .map(netMall -> CompletableFuture.supplyAsync(() ->
                        String.format("《" + productName + "》 in %s price is %.2f",
                                netMall.getNetMallName(),
                                netMall.calcPrice(productName))))
                .collect(Collectors.toList())
                .stream()
                .map(s -> s.join())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        List<String> list1 = getPrice(list, "mysql");
        for (String s : list1) {
            System.out.println(s);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime - startTime) + " 毫秒");

        System.out.println("-----------------------------");

        long startTime1 = System.currentTimeMillis();
        List<String> list2 = getPriceByCompletableFuture(list, "mysql");
        for (String s : list2) {
            System.out.println(s);
        }
        long endTime1 = System.currentTimeMillis();
        System.out.println("costTime: " + (endTime1 - startTime1) + " 毫秒");


    }
}

class NetMall {

    @Getter
    private String netMallName;

    public NetMall(String netMallName) {
        this.netMallName = netMallName;
    }

    public double calcPrice(String productName) {
        // 业务模拟，睡眠一秒钟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }

}