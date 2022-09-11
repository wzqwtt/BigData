package com.book.ch2;

import java.util.concurrent.TimeUnit;

/**
 * 竞态Demo
 *
 * @author wzq
 * @create 2022-09-07 20:15
 */
public class RaceConditionDemo {

    public static void main(String[] args) {
        int numberOfThreads = args.length > 0 ? Integer.parseInt(args[0]) : Runtime.getRuntime().availableProcessors();
        Thread[] workerThreads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            workerThreads[i] = new WorkerThread(i, 10);
        }
        for (int i = 0; i < numberOfThreads; i++) {
            workerThreads[i].start();
        }
    }

    static class WorkerThread extends Thread {

        private final int requestCount;

        public WorkerThread(int id, int requestCount) {
            super("worker-" + id);
            this.requestCount = requestCount;
        }

        @Override
        public void run() {
            int i = requestCount;
            String requestID;
            RequestIDGenerator requestIDGenerator = RequestIDGenerator.getInstance();
            while (i-- > 0) {
                requestID = requestIDGenerator.nextID();
                processRequest(requestID);
            }
        }

        private void processRequest(String requestID) {
            // 模拟请求处理耗时
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("%s got requestID: %s",
                    Thread.currentThread().getName(), requestID));
        }
    }

}
