package com.wzq.base;

/**
 * 测试Thread类中的常用方法:
 * 1、start() 启动当前线程，调用当前线程的run()方法
 * 2、run() 重写此方法，需要执行的代码放在这个方法里面
 * 3、currendThread() 静态方法，会返回当前线程对象
 * 4、getName() 获取当前线程的Name
 * 5、setName() 设置当前线程的Name
 * 6、yield() 释放当前CPU执行权
 * 7、join() 会抛异常，在线程A调用线程B的join，线程A会进入阻塞状态，直到线程B执行完毕，线程A才结束阻塞
 * 8、stop() 强制结束当前线程，已过时，不建议使用
 * 9、sleep() 会抛异常，让当前线程睡眠，需要传递一个参数代表时间，单位是毫秒。在睡眠期间线程阻塞
 * 10、isAlive() 判断当前线程是否存活
 * <p>
 * 线程优先级：
 * MAX_PRIORITY = 10
 * MIN_PRIORITY = 1
 * NORM_PRIORITY = 5
 * 获取和设置线程优先级的方法
 * 11、getPriority() 获取当前线程优先级
 * 12、setPriority() 设置当前线程优先级
 *
 * @author wzq
 * @create 2022-08-03 14:09
 */

class Thread03 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
//                try {
//                    sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println(getName() + " , 优先级 " + getPriority() + ": 偶数" + i);
            }
            if (i % 20 == 0) {
                Thread.yield();
                // 等价于
                // this.yield();
                // Thread.currentThread().yield();
            }
        }
    }

    public Thread03(String name) {
        super(name);
    }

    public Thread03() {
        super();
    }
}

public class ThreadMethodTest {

    public static void main(String[] args) {
//        Thread03 t3 = new Thread03();
//        t3.setName("线程1");
//        t3.start();

        Thread03 t4 = new Thread03("线程1");
        t4.setPriority(Thread.MAX_PRIORITY);    // 设置优先级
        t4.start();

        // 设置main线程的名字
        Thread.currentThread().setName("主线程");
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + " , 优先级 " + Thread.currentThread().getPriority() + ": 奇数" + i);
            }
            if (i == 20) {
                try {
                    t4.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(t4.isAlive());
    }

}
