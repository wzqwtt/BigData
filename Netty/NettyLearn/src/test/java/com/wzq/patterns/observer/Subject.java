package com.wzq.patterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wzq
 * @create 2022-09-02 21:49
 */
public class Subject {

    private List<Observer> observers = new ArrayList<Observer>();
    private int state;

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
        notifyAllObservers();
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
