package com.wzq.patterns.intercepting_filter;

/**
 * 拦截过滤器模式
 *
 * @author wzq
 * @create 2022-11-08 21:56
 */
public interface Filter {
    public void execute(String request);
}
