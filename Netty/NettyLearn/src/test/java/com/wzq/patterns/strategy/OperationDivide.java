package com.wzq.patterns.strategy;

/**
 * @author wzq
 * @create 2022-11-25 14:11
 */
public class OperationDivide implements Strategy {
    @Override
    public int doOperation(int num1, int num2) {
        double res = 0;

        if (num2 != 0) {
            res = (double) num1 / (double) num2;
        } else {
            throw new ArithmeticException("/ by zero");
        }

        return (int) res;
    }
}
