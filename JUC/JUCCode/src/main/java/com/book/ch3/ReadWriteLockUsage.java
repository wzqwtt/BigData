package com.book.ch3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁的使用方法
 *
 * @author wzq
 * @create 2022-09-08 22:43
 */
public class ReadWriteLockUsage {

    // 读写锁
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    // 读锁，共享，多个线程可以同时持有读锁
    private final Lock readLock = rwLock.readLock();
    // 写锁，排他，线程独占临界区
    private final Lock writeLock = rwLock.writeLock();

    // 读线程执行该方法
    public void reader() {
        readLock.lock();
        try {
            // 在此区域读取共享变量
        } finally {
            readLock.unlock();
        }
    }

    // 写线程执行该方法
    public void writer() {
        writeLock.lock();
        try {
            // 在此区域访问（读、写）共享变量
        } finally {
            writeLock.unlock();
        }
    }

}
