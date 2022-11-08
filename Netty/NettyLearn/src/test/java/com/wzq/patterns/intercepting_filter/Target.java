package com.wzq.patterns.intercepting_filter;

/**
 * @author wzq
 * @create 2022-11-08 21:58
 */
public class Target {

    public void execute(String request) {
        System.out.println("Executing request: " + request);
    }

}
