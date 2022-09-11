package com.book.ch2;

import java.util.concurrent.TimeUnit;

/**
 * 可见性问题Demo
 *
 * @author wzq
 * @create 2022-09-08 20:02
 */
public class VisibilityDemo {

    public static void main(String[] args) {
        TimeConsumingTask timeConsumingTask = new TimeConsumingTask();
        Thread thread = new Thread(new TimeConsumingTask());
        thread.start();

        // 指定的时间内任务没有执行结束的画，就将其取消
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timeConsumingTask.cancel();

    }

}

class TimeConsumingTask implements Runnable {

    // main线程对共享变量toCancel的更新对子线程thread而言不可见
//    private boolean toCancel = false;

    // 添加volatile关键字保证变量的可见性
    private volatile boolean toCancel = false;

    @Override
    public void run() {
        while (!toCancel) {
            if (doExecute()) {
                break;
            }
        }
        if (toCancel) {
            System.out.println("Task was canceled.");
        } else {
            System.out.println("Task Done.");
        }
    }

    private boolean doExecute() {
        boolean isDone = false;
        System.out.println("executing...");

        // 模拟实际操作的时间消耗
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isDone;
    }

    public void cancel() {
        toCancel = true;
        System.out.println(this + " canceled");
    }
}