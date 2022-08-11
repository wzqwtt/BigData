package com.wzq.functional;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Java Stream流
 *
 * @author wzq
 * @create 2022-08-10 0:24
 */
public class StreamDemo {

    List<String> list = Arrays.asList("Hello Java", "Hello Stream", "Hell World", "Hello Java");
    List<Integer> listInterger = Arrays.asList(1, 2, 3, 4, 5, 6);

    @Test
    public void test1() {
        List<String> filter = list.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        System.out.println(filter);
    }

    @Test
    public void test2() {
        List<Integer> temp = listInterger.stream().map(i -> i * i).collect(Collectors.toList());
        System.out.println(temp);
    }

    @Test
    public void test3() {

    }

}
