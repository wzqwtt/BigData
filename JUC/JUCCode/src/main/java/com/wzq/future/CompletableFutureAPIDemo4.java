package com.wzq.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wzq
 * @create 2022-08-10 23:03
 */
public class CompletableFutureAPIDemo4 {

    public static void main(String[] args) {
        CompletableFuture<String> palyA = CompletableFuture.supplyAsync(() -> {
            System.out.println("A come in");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "palyA";
        });

        CompletableFuture<String> playB = CompletableFuture.supplyAsync(() -> {
            System.out.println("B come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "palyB";
        });

        // 对比playA和playB谁更快，谁快谁就执行f
        CompletableFuture<String> res = palyA.applyToEither(playB, f -> f + " is winner!");

        System.out.println(res.join());

    }

}
