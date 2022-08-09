本节所有code都可以在[JUCCode](./JUCCode)中找到

# 一、基本概念

- **程序（Program）：** 是为完成特定任务、用某种语言编写的一组指令的集合。即指一段静态的代码，静态对象。

- **进程（Process）：** 程序的一次执行过程，或是正在运行的一个程序。是一个动态的过程：由他自身产生、存在和消亡的过程（生命周期）。**进程作为资源分配的单位，** 系统在运行时会为每个进程分配不同的内存区域。

- **线程（Thread）：** 进程可以进一步细化为线程，是一个程序内部的一条执行路径。
  
  - 若一个进程同一时间并行执行多个线程，就是支持多线程的
  
  - 线程作为调度的执行的单位，每个线程用于独立的运行栈和程序计数器，线程切换的开销小
  
  - 一个进程中的多个线程共享相同的内存单元/内存地址空间，他们从同一堆中分配对象，可以访问相同的变量和对象。这就使得线程间通信更简便、高效。但多个线程操作共享的系统资源可能就会带来安全的隐患。

在Java中，线程共有以下几种状态（在Thread.State中）：

```java
public enum State {
    NEW,         // 新建
    RUNNABLE,    // 准备就绪
    BLOCKED,     // 阻塞 
    WAITING,     // 随时等待
    TIMED_WAITING, // 过时不候
    TERMINATED;  // 终止
}
```

下图很清晰的展示了线程的几种状态之间的关系：

![](img/2022-08-03-22-02-10-image.png)

# 二、线程的创建

## 1、继承Thread类方式

第一种方式创建线程分为以下几步：

- 新建一个类，继承Thread类

- 重写Thread类中的`run()`方法，此线程执行的所有操作都在run方法中

- 创建该类的对象，调用此类的`start()`方法去执行

案例演示：创建一个线程，遍历100以内的所有偶数：

```java
package com.wzq.base;

// 1、创建一个类继承于Thread类的子类
class MyThread extends Thread {

    // 2、重写Thread类中的run()方法
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": 偶数" + i);
            }
        }
    }
}

public class ThreadTest {
    public static void main(String[] args) {
        // 3、创建Thread类的子类的对象
        MyThread t1 = new MyThread();

        // 4、通过此对象去调用start()方法: 1、启动当前线程；2、调用当前线程的run方法
        t1.start();

        // 如下操作仍然在main方法（主线程）中执行
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ": 奇数" + i);
            }
        }
    }
}
```

当然如果觉得新建一个类比较麻烦，也可以使用匿名内部类的方式创建线程：

```java
public class ThreadTest {
    public static void main(String[] args) {
        // 3、创建Thread类的子类的对象
        MyThread t1 = new MyThread();
        // 4、通过此对象去调用start()方法: 1、启动当前线程；2、调用当前线程的run方法
        t1.start();

        // 匿名子类的方式创建线程
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 2 == 0) {
                        System.out.println(Thread.currentThread().getName() + ": 偶数" + i);
                    }
                }
            }
        }.start();

        // 如下操作仍然在main方法（主线程）中执行
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ": 奇数" + i);
            }
        }
    }
}
```

## 2、实现Runnable接口方式

这种方式创建线程分为以下几步：

- 创建一个实现了Runnable接口的类

- 实现类去实现Runnable接口中的run方法

- 创建实现类对象

- 将此对象作为参数传递到Thread类的构造器，创建Thread类对象

- 通过Thread类对象调用start方法

```java
package com.wzq.base;

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
```

匿名内部类的方式：

```java
// 匿名内部类的方式
new Thread(new Runnable() {
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": 偶数 " + i);
            }
        }
    }
}, "线程3").start();).start();
```

## 3、实现Callable方式

与使用Runnable相比，Callable功能更强大一些：

- 相比run方法，可以有返回值

- 方法可以抛出异常

- 支持泛型的返回值

- 需要借助FutureTask类，比如获取返回结果

Future接口：

- 可以对具体Runnable、Callable任务的执行结果进行取消、查询是否完成、获取结果等

- FutrueTask是Futrue接口的唯一的实现类

- FutureTask同时实现了Runnable，Futrue接口。它既可以作为Runnable被线程执行，又可以作为Future得到Callable的返回值

```java
package com.wzq.base;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
```

## 4、线程池

线程池是什么？

- 背景：经常创建和销毁、使用量特别大的资源，比如并发情况下的线程，对性能影响很大。

- 思路：提前创建好多个线程，放入线程池中，使用时直接获取，使用完放回池中。可以避免频繁创建销毁、实现重复利用。类似生活中的公共交通工具。

- 好处：
  
  - 提高响应速度（减少了创建新线程的时间）
  
  - 降低资源消耗（重复利用线程池中线程，不需要每次都创建）
  
  - 便于线程管理
    
    - corePoolSize：核心池大小
    
    - maximumPoolSize：最大线程数
    
    - keepAliveTime：线程没有任务时最多保持多长时间后会终止
    
    - ...

Java的线程池：

![](img/2022-08-05-17-46-29-7058b9ec2ecad599a50a98e810e4cbc.jpg)

code：

```java
package com.wzq.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class NumThread1 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
    }
}

class NumThread2 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
    }
}

public class ThreadPool {
    public static void main(String[] args) {
        // 1、提供指定线程数量的线程池
        ExecutorService service = Executors.newFixedThreadPool(10);

        // 2、执行指定的线程的操作，需要提供实现Runnable接口或Callable接口实现类的对象
        // 适合用于Runnable
        service.execute(new NumThread1());
        service.execute(new NumThread2());

        // service.submit(Callable callable); // 适用于Callable

        // 3、关闭连接池
        // 如果线程池不用了，可以关闭
        service.shutdown();
    }
}
```

# 三、Thread类常用方法

本节中的测试代码可以在[JUCCode ThreadMethodTest](./JUCCode/src/main/java/com/wzq/base/ThreadMethodTest.java)文件中找到

- `start()`：启动当前线程，运行当前线程的run方法

- `run()`：重写此方法，需要执行的代码放在这个方法里面

- `currentThread()`：静态方法，会返回当前线程对象

- `getName()`：获取当前线程的Name

- `setName()`：设置当前线程的Name

- `yield()`：当前线程释放CPU执行权

- `join()`：会抛异常，在线程A调用线程B的join，线程A会进入阻塞状态，直到线程B执行完毕，线程A才结束阻塞

- `stop()`：强制结束当前线程，已过时，不建议使用

- `sleep()`：会抛异常，让当前线程睡眠，需要传递一个参数代表时间，单位是毫秒。在睡眠期间线程阻塞

- `isAlive()`：判断当前线程是否存活

 

# 四、线程的同步

本节代码可以在[JUCCode ThreadWindowTest1.java](./JUCCode/src/main/java/com/wzq/base/ThreadWindowTest1.java)中找到

在本节开始之前，先做一个小案例：有三个窗口卖票，总票数为100张

那我们创建三个线程当作三个窗口呗，还是很好写的：

```java
package com.wzq.base;

class Window1 implements Runnable {

    private int ticket = 100;

    @Override
    public void run() {
        while (true) {
            if (ticket > 0) {
                // 线程睡眠100毫秒，模拟线程安全问题
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + "卖票，票号为" + ticket);
                ticket--;
            } else {
                System.out.println("暂无余票！");
                break;
            }
        }
    }
}

public class ThreadWindowTest1 {

    public static void main(String[] args) {
        Window1 window = new Window1();
        Thread t1 = new Thread(window, "窗口1");
        Thread t2 = new Thread(window, "窗口2");
        Thread t3 = new Thread(window, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
```

但是这样做会出现卖错票、重票的问题，即线程安全问题：

![](img/2022-08-04-16-48-19-image.png)

这种问题出现的原因是：当其中一个线程操作ticket变量的时候，其他线程也参与了进来也开始操作车票了，因为这个ticket变量是共享变量，因此出现了线程安全问题

解决线程安全问题的手段是：**线程的同步机制**，即当某线程操作共享变量的时候，其他线程不可以进行操作

## 1、方法一：同步代码块

这种方法就是在run方法中，把所有线程使用的共享变量都用`synchronized`包裹起来：

```java
synchronized (同步监视器) {
    // 需要被同步的代码
}
```

需要注意的是：

- 操作共享数据的代码，就是需要被同步的代码 --> 不能包含多了，也不能包含代码少了

- 共享数据：多个线程共同操作的变量。比如：上述卖票案例的ticket变量就是共享数据

- 同步监视器，俗称：锁。任何一个类的对象，都可以充当锁。但是**多个线程必须共有一把锁！**

- 在实现Runnable接口创建多线程的方式中，可以考虑使用`this`充当监视器（锁）

- 在继承Thread类创建多线程的方式中，要慎用`this`充当监视器（锁），可以考虑使用当前类充当（`Window.class`）

有了方法论的指导，就可以写代码了，使用基于Runnable的方式：

```java
package com.wzq.base;

class Window1 implements Runnable {

    private int ticket = 100;
    Object obj = new Object();  // 锁

    @Override
    public void run() {
        while (true) {
            // 同步代码块
            // 同样，这把锁可以是当前对象，即this
            // synchronized (this) {}
            synchronized (obj) {
                if (ticket > 0) {
                    // 线程睡眠100毫秒，模拟线程安全问题
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + "卖票，票号为" + ticket);
                    ticket--;
                } else {
                    System.out.println("暂无余票！");
                    break;
                }
            }
        }
    }
}

public class ThreadWindowTest1 {

    public static void main(String[] args) {
        Window1 window = new Window1();
        Thread t1 = new Thread(window, "窗口1");
        Thread t2 = new Thread(window, "窗口2");
        Thread t3 = new Thread(window, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
```

基于继承Thread类的方式：

```java
package com.wzq.base;

class Window extends Thread {

    private static int ticket = 100;

    private static Object obj = new Object();

    @Override
    public void run() {
        while (true) {
            // synchronized (obj) {
            synchronized (Window.class) {
                if (ticket > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "卖票，票号为: " + ticket);
                    ticket--;
                } else {
                    System.out.println("已无多余的票");
                    break;
                }
            }
        }
    }

    // 构造方法：用于给线程起名字
    public Window(String name) {
        super(name);
    }

}

public class ThreadWindowTest {

    public static void main(String[] args) {
        Window w1 = new Window("窗口1");
        Window w2 = new Window("窗口2");
        Window w3 = new Window("窗口3");

        w1.start();
        w2.start();
        w3.start();
    }

}
```

## 2、方法二：同步方法

如果操作共享数据的代码完整的声明在一个方法中，不妨将此方法声明为同步的

需要注意的是：

- 同步方法仍然涉及到同步监视器（锁），只是不需要我们显示的声明

- 非静态的同步方法，同步监视器是：`this`

- 静态的同步方法，同步监视器是：当前类本身

使用基于Runnable的方法：

```java
package com.wzq.base;

class Window2 implements Runnable {

    private int ticket = 100;

    @Override
    public void run() {
        while (true) {
            if (ticket <= 0) {
                System.out.println("暂无余票！");
                break;
            }
            show();
        }
    }

    // 此刻同步监视器为this
    private synchronized void show() {
        if (ticket > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + "卖票，票号为：" + ticket);
            ticket--;
        }
    }
}

public class ThreadWindowTest2 {

    public static void main(String[] args) {
        Window2 w2 = new Window2();

        Thread t1 = new Thread(w2, "窗口1");
        Thread t2 = new Thread(w2, "窗口2");
        Thread t3 = new Thread(w2, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
```

使用基于继承Thread的方式：

```java
package com.wzq.base;

class Window3 extends Thread {

    private static int ticket = 100;

    @Override
    public void run() {
        while (true) {
            if (ticket <= 0) {
                System.out.println("暂无余票！");
                break;
            }
            show();
        }
    }

    // 同步方法，此刻同步监视器为 当前类对象 Window3.class
    private static synchronized void show() {
        if (ticket > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "卖票，票号：" + ticket);
            ticket--;
        }
    }

    // 构造方法，传递线程name
    public Window3(String name) {
        super(name);
    }

}

public class ThreadWindowTest3 {
    public static void main(String[] args) {
        Window3 t1 = new Window3("窗口1");
        Window3 t2 = new Window3("窗口2");
        Window3 t3 = new Window3("窗口3");

        t1.start();
        t2.start();
        t3.start();
    }
}
```

## 3、方法三：Lock锁

出现这种方式的锁是因为synchronized容易出现死锁，所谓死锁就是：不同的线程分别占用对方需要的同步资源不放弃，都在等待对方放弃自己需要的同步资源，就形成了线程的死锁

出现死锁以后，不会出现异常，不会报错，只是所有的线程都处于阻塞状态，程序无法继续进行。在使用同步的时候，应该尽全力避免死锁的出现。

死锁的code在[JUCCode DeadLock.java](./JUCCode/src/main/java/com/wzq/base/DeadLock.java)中，大家可以自己体会，不建议写，万一写熟练了怎么办……

所以就诞生了Lock锁的方式：

- 从JDK 5.0开始，Java提供了更强大的线程同步机制--通过显式定义同步锁对象来实现同步。同步锁使用Lock对象充当

- java.util.concurrent.locks.Lock接口是控制多个线程对共享资源进行访问的工具。锁提供了对共享资源的独占访问，每次只能有一个线程对Lock对象加锁，线程开始访问共享资源之前应先获得Lock对象

- ReentrantLock类实现了Lock，它拥有与 synchronized相同的并发性和内存语义，在实现线程安全的控制中，比较常用的是 Reentrantlock，可以显式加锁、释放锁

其实现方式是这样的：

```java
class A implements Runnable {
    // 1、实例化ReentrantLock对象
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        try {
            // 2、上锁
            lock.lock();
            // ... code
        } finally {
            // 3、释放锁
            lock.unlock();
        }
    }
}
```

卖票案例：

```java
package com.wzq.base;

import java.util.concurrent.locks.ReentrantLock;

class Window4 implements Runnable {

    private int ticket = 100;
    // 1、实例化对象
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        while (true) {
            try {
                // 2、加锁
                lock.lock();
                if (ticket > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "卖票，票号为：" + ticket);
                    ticket--;
                } else {
                    System.out.println("暂无余票！");
                    break;
                }
            } finally {
                // 3、解锁
                lock.unlock();
            }
        }
    }
}

public class ThreadWindowTest4 {

    public static void main(String[] args) {
        Window4 w = new Window4();

        Thread t1 = new Thread(w, "窗口1");
        Thread t2 = new Thread(w, "窗口2");
        Thread t3 = new Thread(w, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

}
```

## 4、总结

有以下一个问题需要解答：

- synchronized与lock的异同：
  
  - 相同点：二者都可以解决线程安全问题
  
  - 不同点：
    
    - syschronized机制在执行完相应的代码后，自动释放锁
    
    - lock需要手动开锁（启动同步），手动解锁（关闭同步）

使用同步的顺序：Lock---> 同步代码块（已经进入了方法体，分配了相应资源 ) --->同步方法（在方法体之外)

# 五、线程的通信

线程通信的例子：使用两个线程交替打印 1-100 中的数字

线程的通信涉及三个方法：

- `wait()`：一旦执行此方法，当前线程就会进入阻塞状态，并且释放锁

- `notify()`：一旦执行此方法，就会唤醒被wait的一个线程；如果多个线程被wait，那么释放优先级最高的那个线程

- `notifyAll()`：一旦执行此方法，就会唤醒所有被wait的线程

所以就可以写代码了：

```java
package com.wzq.base;

class Number implements Runnable {
    private int number = 1;

    @Override
    public void run() {
        while (true) {
            // 锁
            synchronized (this) {
                notify();   // 唤醒另一个线程
                if (number <= 100) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + ": " + number);
                    number++;

                    try {
                        wait(); // 该线程进入阻塞状态，并释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }
}

public class CommunicationTest {

    public static void main(String[] args) {
        Number number = new Number();

        Thread t1 = new Thread(number, "线程1");
        Thread t2 = new Thread(number, "线程2");

        t1.start();
        t2.start();
    }
}
```

关于这三个方法有以下几点需要注意的：

- 这三个方法都必须使用在同步代码块或同步方法中

- 这三个方法的调用者必须是 同步代码块或同步方法的锁（同步监视器），否则会出现IllegalMonitorStateException异常

- 这三个方法被定义在 java.lang.Object中

问：sleep()和wait()的异同：

- 相同点：都可以使线程进入阻塞状态

- 不同点：
  
  - 两个方法声明的位置不同：Thread类中声明sleep()，Object类中声明wait()
  
  - 调用的要求不同：sleep可以在任何需要的场景下使用，但wait必须使用在同步代码块或同步方法中
  
  - 如果两个方法都被声明在同步代码块或同步方法中，sleep不释放锁，wait释放

# 六、单例模式的线程安全

只需要声明getInstance方法是`synchronized`的就可以了：

```java
class Singleton {
    // 构造器私有化
    private Singleton() {
    }

    private static Singleton instance = null;

    // 锁为Singleton.class
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

# 七、案例

**案例：** 银行有一个账户，有两个储户分别向同一个账户存3000元，每次存1000块，存3次。每次存完打印账户余额

code：

- 实现Runnable的code在[JUCCode AccountTest.java](./JUCCode/src/main/java/com/wzq/base/AccountTest.java)中

- 单例模式+继承Thread类的code在[JUCCode AccountTest1.java](./JUCCode/src/main/java/com/wzq/base/AccountTest1.java)中

- 单例模式+lock锁+实现Runnable类的code在[JUCCode AccountTest2.java](./JUCCode/src/main/java/com/wzq/base/AccountTest2.java)中

最终效果：

![](img/2022-08-05-00-03-41-image.png)

# 参考资料

- [尚硅谷Java Thread类基础](https://www.bilibili.com/video/BV1Kb411W75N?p=413)
