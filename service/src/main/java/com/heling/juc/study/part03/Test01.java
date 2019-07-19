package com.heling.juc.study.part03;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: wangheling
 * @Date: 2019/7/10 13:18
 * @Description: 通过栅栏，计数器模拟高并发场景：比较Random、ThreadLocalRandom和ThreadLocal<Random>效率
 * <p>
 * 不能多个线程共享ThreadLocalRandom，否则随机值一样
 * ThreadLocalRandom底层类似于ThreadLocal<Random>这种方式，
 * 相当于每个线程有一个random，从而防止多个线程竞争一个种子而影响效率
 */
public class Test01 {

    /**
     * Random
     */
    public static class RandomThread implements Runnable {

        private final CountDownLatch latch;

        private final CyclicBarrier barrier;

        private final Random random;

        public RandomThread(final Random random, final CountDownLatch latch, final CyclicBarrier barrier) {
            this.random = random;
            this.latch = latch;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                random.nextInt(100);
            }
            latch.countDown();
        }
    }

    /**
     * ThreadLocalRandom
     */
    public static class ThreadLocalRandomThread implements Runnable {

        private final CountDownLatch latch;

        private final CyclicBarrier barrier;

        public ThreadLocalRandomThread(final CountDownLatch latch, final CyclicBarrier barrier) {
            this.latch = latch;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            //不能多个线程共享ThreadLocalRandom
            final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
            for (int i = 0; i < 10; i++) {
                threadLocalRandom.nextInt(100);
            }
            latch.countDown();
        }
    }

    /**
     * ThreadLocal<Random>
     */
    public static class ThreadLocal_RandomThread implements Runnable {

        private final CountDownLatch latch;

        private final CyclicBarrier barrier;

        public ThreadLocal_RandomThread(final CountDownLatch latch, final CyclicBarrier barrier) {
            this.latch = latch;
            this.barrier = barrier;
        }


        //设置线程本地random
        private final ThreadLocal<Random> THL_RANDOM = new ThreadLocal<Random>() {
            @Override
            protected Random initialValue() {
                return new Random(100);
            }
        };

        @Override
        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            final Random random = THL_RANDOM.get();
            for (int i = 0; i < 10; i++) {
                random.nextInt();
            }
            latch.countDown();
        }
    }

    /**
     * 结果：后者比前者效率高
     * random并发耗时：271ms
     * threadLocalRandom并发耗时：159ms
     * ThreadLocal_RandomThread并发耗时：147ms
     */
    public static void main(String[] args) throws InterruptedException {
        //并发线程数
        final int threads = 1000;

        //计数器：保证所有线程都执行完
        final CountDownLatch latch = new CountDownLatch(threads);
        final CountDownLatch latch2 = new CountDownLatch(threads);
        final CountDownLatch latch3 = new CountDownLatch(threads);

        //栅栏：拦住所有线程，达到并发的效果
        final CyclicBarrier barrier = new CyclicBarrier(threads);
        final CyclicBarrier barrier2 = new CyclicBarrier(threads);
        final CyclicBarrier barrier3 = new CyclicBarrier(threads);

        //=================测试random并发耗时开始======================
        long start = System.currentTimeMillis();
        final Random random = new Random();
        for (int i = 0; i < threads; i++) {
            new Thread(new RandomThread(random, latch, barrier)).start();
        }
        latch.await();
        System.out.println("random并发耗时：" + (System.currentTimeMillis() - start) + "ms");
        //=================测试random并发耗时结束======================

        //=================测试ThreadLocalRandom并发耗时开始======================
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(new ThreadLocalRandomThread(latch2, barrier2));
            thread.start();
        }
        latch2.await();
        System.out.println("threadLocalRandom并发耗时：" + (System.currentTimeMillis() - start2) + "ms");
        //=================测试ThreadLocalRandom并发耗时结束======================

        //=================测试ThreadLocal<Random>并发耗时开始======================
        long start3 = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(new ThreadLocal_RandomThread(latch3, barrier3));
            thread.start();
        }
        latch3.await();
        System.out.println("ThreadLocal_RandomThread并发耗时：" + (System.currentTimeMillis() - start3) + "ms");
        //=================测试ThreadLocal<Random>并发耗时结束======================
    }

}
