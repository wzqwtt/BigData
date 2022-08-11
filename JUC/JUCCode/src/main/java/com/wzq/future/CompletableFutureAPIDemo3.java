package com.wzq.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wzq
 * @create 2022-08-10 22:18
 */
public class CompletableFutureAPIDemo3 {

    public static void main(String[] args) {
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenRun(() -> {}).join());
        System.out.println("----------");
        System.out.println(CompletableFuture.supplyAsync(() -> "resultB").thenAccept(s -> System.out.println(s)).join());
        System.out.println("----------");
        System.out.println(CompletableFuture.supplyAsync(() -> "resultC").thenApply(s -> s + "C").join());
    }

}
