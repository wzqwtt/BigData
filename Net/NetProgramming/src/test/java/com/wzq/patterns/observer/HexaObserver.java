package com.wzq.patterns.observer;

/**
 * @author wzq
 * @create 2022-09-02 21:57
 */
public class HexaObserver extends Observer {

    public HexaObserver(Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void update() {
        System.out.println("Hex String : " + Integer.toHexString(subject.getState()).toUpperCase());
    }
}
