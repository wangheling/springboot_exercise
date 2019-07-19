package com.heling.juc.study.part01;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Author: wangheling
 * @Date: 2019/7/3 11:37
 * @Description: 使用wait、notify实现生产者消费者demo
 * tips:1.自定义线程工厂
 * 2.拒绝策略：丢弃策略测试
 * 3.自定义拒绝策略（不丢弃任务）：测试
 */
@Slf4j
public class Test01 {

    public static void main(String[] args) {

        final int MAX_SIZE = 100;

        //阻塞队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(MAX_SIZE);

        //自定义线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                4,
                10,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(20),
                //自定义线程工厂
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "测试线程");
                    }
                },
                //自定义拒绝策略，保证所有任务都要完成，不丢弃
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            //核心改造点，由blockingqueue的offer改成put阻塞方法
                            executor.getQueue().put(r);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        int count = 0;
        while (count < 1000) {
//            try {
//                //使用默认拒绝策略，防止消费者来不及消费而拒绝任务，放慢生产者生产速度
//                TimeUnit.MICROSECONDS.sleep(5L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            threadPoolExecutor.submit(new Producer(queue, MAX_SIZE));
            threadPoolExecutor.submit(new Consumer(queue, MAX_SIZE));
            count++;
        }
        threadPoolExecutor.shutdown();
    }

    /**
     * 生产者
     */
    public static class Producer implements Runnable {

        private BlockingQueue<String> queue;

        private int queueMaxSize;

        public Producer(BlockingQueue<String> queue, int queueMaxSize) {
            this.queue = queue;
            this.queueMaxSize = queueMaxSize;
        }

        @Override
        public void run() {
            synchronized (queue) {

                while (queue.size() == queueMaxSize) {
                    //当队列长度为最大时候,停止生产
                    try {
                        log.info("队列满了,生产者进行等待");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("生产者开始生产");
                queue.add(String.valueOf(System.currentTimeMillis()));
                //通知消费者消费
                queue.notifyAll();
            }

        }
    }


    /**
     * 消费者
     */
    public static class Consumer implements Runnable {
        private BlockingQueue<String> queue;

        private int queueMaxSize;

        public Consumer(BlockingQueue<String> queue, int queueMaxSize) {
            this.queue = queue;
            this.queueMaxSize = queueMaxSize;
        }

        @Override
        public void run() {
            synchronized (queue) {
                while (queue.size() == 0) {
                    //当队列长度为0时候,停止消费
                    try {
                        log.info("队列空了,消费者进行等待");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("消费者开始消费");
                String ele = queue.poll();
                log.info("消费者从队列拿出:{}", ele);
                //通知生产者生产
                queue.notifyAll();
            }
        }

    }
}
