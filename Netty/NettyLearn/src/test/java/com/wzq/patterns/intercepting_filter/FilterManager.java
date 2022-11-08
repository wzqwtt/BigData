package com.wzq.patterns.intercepting_filter;

/**
 * @author wzq
 * @create 2022-11-08 21:59
 */
public class FilterManager {

    FilterChain filterChain;

    public FilterManager(Target target) {
        filterChain = new FilterChain();
        filterChain.setTarget(target);
    }

    public void setFilter(Filter filter){
        filterChain.addFilter(filter);
    }

    public void filterRequest(String request) {
        filterChain.execute(request);
    }

}
