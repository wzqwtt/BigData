package com.wzq.patterns.observer;

/**
 * @author wzq
 * @create 2022-09-02 21:53
 */
public abstract class Observer {

    protected Subject subject;

    public abstract void update();

}
