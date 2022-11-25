package com.wzq.patterns.strategy;

/**
 * @author wzq
 * @create 2022-11-25 14:08
 */
public class Context {

    private Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public int executeStrategy(int num1, int num2) {
        return strategy.doOperation(num1, num2);
    }

}
