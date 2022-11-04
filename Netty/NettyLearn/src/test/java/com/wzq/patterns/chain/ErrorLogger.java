package com.wzq.patterns.chain;

/**
 * @author wzq
 * @create 2022-11-04 13:51
 */
public class ErrorLogger extends AbstactLogger {

    public ErrorLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("Error Console::Logger: " + message);
    }
}
