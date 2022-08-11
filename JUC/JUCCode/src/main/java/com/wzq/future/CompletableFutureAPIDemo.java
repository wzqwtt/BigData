package com.wzq.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wzq
 * @create 2022-08-10 21:29
 */
public class CompletableFutureAPIDemo {

    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "abc";
        });

//        System.out.println(completableFuture.get());
//        System.out.println(completableFuture.get(3, TimeUnit.SECONDS));
//        System.out.println(completableFuture.join());

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println(completableFuture.getNow("xxx"));

        System.out.println(completableFuture.complete("completeValue") + "\t" + completableFuture.join());

    }

}
