package com.wzq.patterns.chain;

/**
 * @author wzq
 * @create 2022-11-04 13:52
 */
public class FileLogger extends AbstactLogger {

    public FileLogger(int level) {
        this.level = level;
    }

    @Override
    protected void write(String message) {
        System.out.println("File::Logger: " + message);
    }
}
