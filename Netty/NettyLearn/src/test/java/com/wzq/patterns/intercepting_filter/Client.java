package com.wzq.patterns.intercepting_filter;

/**
 * @author wzq
 * @create 2022-11-08 22:01
 */
public class Client {

    FilterManager filterManager;

    public void setFilterManager(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    public void sendRequest(String request) {
        filterManager.filterRequest(request);
    }

}
