package com.wzq.patterns.strategy;

/**
 * @author wzq
 * @create 2022-11-25 14:07
 */
public class OperationMultiply implements Strategy {
    @Override
    public int doOperation(int num1, int num2) {
        return num1 * num2;
    }
}
