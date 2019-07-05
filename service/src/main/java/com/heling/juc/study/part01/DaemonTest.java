package com.heling.juc.study.part01;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: wangheling
 * @Date: 2019/7/4 17:28
 * @Description: 守护和非守护线程学习
 */
public class DaemonTest {

    public static void main(String[] args) throws InterruptedException {

        AtomicInteger count = new AtomicInteger(1);

        for (int i = 0; i < 10; i++) {
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println(count.getAndIncrement() + "子线程运行");
//                }
//            });
            Thread thread = new Thread(() -> System.out.println(count.getAndIncrement() + "子线程运行"));
            //默认非守护线程
            /**
             * 主线程结束，子线程不会结束，会全部执行完
             * 1子线程运行
             * 2子线程运行
             * 3子线程运行
             * ===主线程结束===
             * 4子线程运行
             * 5子线程运行
             * 6子线程运行
             * 7子线程运行
             * 8子线程运行
             * 9子线程运行
             * 10子线程运行
             */
            thread.setDaemon(false);


            /**
             * 主线程结束，已经开始的子线程会执行完，没有开始的则不会开始
             * 2子线程运行
             * ===主线程结束===
             * 1子线程运行
             * 3子线程运行
             */
//            thread.setDaemon(true);
            System.out.println(thread.isDaemon());
            thread.start();
        }
        TimeUnit.MICROSECONDS.sleep(1L);
        System.out.println("===主线程结束===");


    }

}
