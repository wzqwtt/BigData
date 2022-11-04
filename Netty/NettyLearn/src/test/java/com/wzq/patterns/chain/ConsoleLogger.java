package com.wzq.patterns.chain;

/**
 * @author wzq
 * @create 2022-11-04 13:50
 */
public class ConsoleLogger extends AbstactLogger {

    public ConsoleLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("Standard Console::Logger: " + message);
    }
}
