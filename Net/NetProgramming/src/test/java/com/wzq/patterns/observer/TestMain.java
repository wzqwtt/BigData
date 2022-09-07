package com.wzq.patterns.observer;

/**
 * @author wzq
 * @create 2022-09-02 21:58
 */
public class TestMain {
    public static void main(String[] args) {
        Subject subject = new Subject();

        new HexaObserver(subject);
        new OctalObserver(subject);
        new BinaryObserver(subject);

        System.out.println("First state change : 15");
        subject.setState(15);
        System.out.println("Second state change : 10");
        subject.setState(10);
    }
}
