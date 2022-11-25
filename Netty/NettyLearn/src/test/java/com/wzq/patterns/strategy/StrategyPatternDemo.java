package com.wzq.patterns.strategy;

/**
 * @author wzq
 * @create 2022-11-25 14:09
 */
public class StrategyPatternDemo {

    public static void main(String[] args) {

        Context context = new Context(new OperationAdd());
        System.out.println("10 + 5 = " + context.executeStrategy(10, 5));

        context = new Context(new OperationSubtract());
        System.out.println("10 - 5 = " + context.executeStrategy(10, 5));

        context = new Context(new OperationMultiply());
        System.out.println("10 * 5 = " + context.executeStrategy(10, 5));

        context = new Context(new OperationDivide());
        System.out.println("10 / 5 = " + context.executeStrategy(10, 5));
        System.out.println("10 / 0 = " + context.executeStrategy(10, 0));
    }

}
