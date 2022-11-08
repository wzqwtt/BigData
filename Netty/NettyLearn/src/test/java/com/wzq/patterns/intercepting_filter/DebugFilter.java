package com.wzq.patterns.intercepting_filter;

/**
 * @author wzq
 * @create 2022-11-08 21:57
 */
public class DebugFilter implements Filter {
    @Override
    public void execute(String request) {
        System.out.println("request log: " + request);
    }
}
