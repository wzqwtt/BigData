package com.wzq.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 1、get容易阻塞，一般建议放在程序后面，一旦调用不见不散，非要等到结果才会离开，不管是否计算完成，容易程序阻塞
 * 2、加入不愿意等很长实践，希望过时不候，可以自动离开，get(timeout,TimeUnit)
 *
 * @author wzq
 * @create 2022-08-08 0:34
 */
public class FutureTaskGetDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask<String> task = new FutureTask<String>(() -> {
            System.out.println(Thread.currentThread().getName() + " ---- come in");
            Thread.sleep(5000);
            return "111";
        });

        Thread t1 = new Thread(task, "t1");

        t1.start();

        System.out.println(Thread.currentThread().getName() + " over");

//        String s = task.get();     // 在此get线程会阻塞
//        System.out.println(s);

//        task.get(3, TimeUnit.SECONDS);  // 设置过时不候

        while (true) {
            if (task.isDone()) {
                String s = task.get();
                System.out.println(s);
                break;
            } else {
                Thread.sleep(500);
                System.out.println("正在执行线程" + t1.getName());
            }
        }

    }

}
