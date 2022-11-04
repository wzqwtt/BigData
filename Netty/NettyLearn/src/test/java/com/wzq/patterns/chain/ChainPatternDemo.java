package com.wzq.patterns.chain;

/**
 * @author wzq
 * @create 2022-11-04 14:02
 */
public class ChainPatternDemo {

    private static AbstactLogger getChainOfLoggers() {
        ErrorLogger errorLogger = new ErrorLogger(AbstactLogger.ERROR);
        FileLogger fileLogger = new FileLogger(AbstactLogger.DEBUG);
        ConsoleLogger consoleLogger = new ConsoleLogger(AbstactLogger.INFO);

        errorLogger.setNextLogger(fileLogger);
        fileLogger.setNextLogger(consoleLogger);

        return errorLogger;
    }

    public static void main(String[] args) {
        AbstactLogger loggerChain = getChainOfLoggers();

        loggerChain.logMessage(AbstactLogger.INFO, "This is an information.");

        loggerChain.logMessage(AbstactLogger.DEBUG, "This is a debug level information.");

        loggerChain.logMessage(AbstactLogger.ERROR, "This is an error information");
    }

}
