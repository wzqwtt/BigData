package com.wzq.base;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 实现Callable接口创建线程
 *
 * @author wzq
 * @create 2022-08-05 16:57
 */
// 1、创建一个实现了Callable类的实现类
class NumThread implements Callable<Integer> {

    // 2、实现call方法，将此线程需要做的操作声明在call方法中
    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + "线程，i=" + i);
                sum += i;
            }
        }
        return sum;
    }
}

public class ThreadCreate3 {

    public static void main(String[] args) {
        // 3、创建Callable实现类的对象
        NumThread numThread = new NumThread();
        // 4、将此Callable接口实现类的对象传递到FutureTask构造器中，创建FutureTask对象
        FutureTask<Integer> futureTask = new FutureTask<Integer>(numThread);
        // 5、将futureTask对象作为参数传递到Threa类中，并启动
        new Thread(futureTask).start();

        try {
            // 6、获取最终结果
            Integer sum = futureTask.get();
            System.out.println("总和为：" + sum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
