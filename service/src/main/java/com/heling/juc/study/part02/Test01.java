package com.heling.juc.study.part02;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author whl
 * @description 测试自旋锁
 * @date 2019/07/09 21:18
 */
public class Test01 {

    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReentrantLock lock2 = new ReentrantLock();


    public static void mehtodC() {
        try {
            lock.lock();
            System.out.println("methodC");
            methodD();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    private static void methodD() {
        try {
            lock.lock();
            System.out.println("methodD");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static synchronized void methodA() {
        System.out.println("methodA");
        methodB();
    }

    private static synchronized void methodB() {
        System.out.println("methodB");
    }

    public static void main(String[] args) {
        //证明synchronized是重入锁
        methodA();
        //说明ReentrantLock也是重入锁
        mehtodC();
    }

}
