package com.wzq.patterns.intercepting_filter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 过滤器链
 *
 * @author wzq
 * @create 2022-11-08 21:58
 */
public class FilterChain {

    private List<Filter> filters = new ArrayList<>();
    private Target target;

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void execute(String request) {
        for (Filter filter : filters) {
            filter.execute(request);
        }
        target.execute(request);
    }

    public void setTarget(Target target){
        this.target = target;
    }

}
