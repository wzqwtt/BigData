package com.wzq.patterns.intercepting_filter;

/**
 * 实体过滤器
 *
 * @author wzq
 * @create 2022-11-08 21:56
 */
public class AutenticationFilter implements Filter {
    @Override
    public void execute(String request) {
        System.out.println("Authenticating request: " + request);
    }
}
