package com.heling.juc.study.part01;

import java.util.concurrent.TimeUnit;

/**
 * @author whl
 * @description 测试notify和notifyAll方法的区别
 * @date 2019/07/02 22:19
 */
public class Test02 {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread threadA = new Thread(() -> {
            synchronized (lock) {
                System.out.println("a获得了锁");
                System.out.println("a开始wait");
                try {
                    lock.wait();
                    System.out.println("a结束wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "threadA");

        Thread threadB = new Thread(() -> {
            synchronized (lock) {
                System.out.println("b获得了锁");
                System.out.println("b开始wait");
                try {
                    lock.wait();
                    System.out.println("b结束wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "threadB");

        Thread threadC = new Thread(() -> {
            synchronized (lock) {
                System.out.println("c获得了锁");
                System.out.println("c开始通知");
//                    lock.notify();
                lock.notifyAll();
            }
        }, "threadC");

        threadA.start();
        threadB.start();

        TimeUnit.SECONDS.sleep(1L);

        threadC.start();

        //等待所有线程执行完
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("main方法结束");
    }


}
