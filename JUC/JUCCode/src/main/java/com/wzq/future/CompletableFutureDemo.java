package com.wzq.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author wzq
 * @create 2022-08-07 22:24
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<>(new MyThread());

        Thread t1 = new Thread(futureTask, "t1");
        t1.start();

        try {
            String s = futureTask.get();
            System.out.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

class MyThread implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("----come in call()");
        return "hello Callable";
    }
}