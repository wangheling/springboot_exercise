package com.heling.juc.study.part03;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Auther: wangheling
 * @Date: 2019/7/10 13:18
 * @Description: 通过栅栏，计数器模拟高并发场景：验证Random比ThreadLocalRandom效率低
 * <p>
 * 不能多个线程共享ThreadLocalRandom，否则随机值一样
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
     * 结果：后者比前者效率高
     * random并发耗时：314ms
     * threadLocalRandom并发耗时：141ms
     */
    public static void main(String[] args) throws InterruptedException {
        //并发线程数
        final int threads = 1000;

        //计数器：保证所有线程都执行完
        final CountDownLatch latch = new CountDownLatch(threads);
        final CountDownLatch latch2 = new CountDownLatch(threads);

        //栅栏：拦住所有线程，达到并发的效果
        final CyclicBarrier barrier = new CyclicBarrier(threads);
        final CyclicBarrier barrier2 = new CyclicBarrier(threads);

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
    }

}
