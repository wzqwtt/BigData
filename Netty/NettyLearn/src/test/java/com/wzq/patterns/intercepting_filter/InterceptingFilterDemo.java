package com.wzq.patterns.intercepting_filter;

/**
 * @author wzq
 * @create 2022-11-08 22:01
 */
public class InterceptingFilterDemo {

    public static void main(String[] args) {
        FilterManager filterManager = new FilterManager(new Target());
        filterManager.setFilter(new AutenticationFilter());
        filterManager.setFilter(new DebugFilter());

        Client client = new Client();
        client.setFilterManager(filterManager);
        client.sendRequest("HOME");
    }

}
