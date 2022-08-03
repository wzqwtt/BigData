package com.wzq.base;

/**
 * 创建多线程的第二种方式：
 * 1、创建一个实现了Runnable接口的类
 * 2、实现类去实现Runnable中的抽象方法：run()
 * 3、创建实现类的对象
 * 4、将此对象作为参数传递到Thread类的构造器，创建Thread类的对象
 * 5、通过Thread类的对象调用start()
 *
 * @author wzq
 * @create 2022-08-03 16:09
 */

// 1、创建一个实现了Runnable接口的类
class Thread05 implements Runnable {

    // 2、实现类去实现Runnable中的抽象方法：run()
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": 偶数 " + i);
            }
        }
    }
}

public class ThreadCreate2 {

    public static void main(String[] args) {
        // 3、创建实现类的对象
        Thread05 thread05 = new Thread05();
        // 4、将此对象作为参数传递到Thread类的构造器，创建Thread类的对象
        Thread t1 = new Thread(thread05);
        t1.setName("线程1");  // 设置线程名字
        // 5、通过Thread类的对象调用start()
        t1.start();

        Thread t2 = new Thread(thread05);
        t2.setName("线程2");
        t2.start();
    }

}
