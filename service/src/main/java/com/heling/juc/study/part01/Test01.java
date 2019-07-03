package com.heling.juc.study.part01;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Auther: wangheling
 * @Date: 2019/7/3 11:37
 * @Description: 使用wait、notify实现生产者消费者demo
 */
@Slf4j
public class Test01 {

    public static void main(String[] args) {

        int MAX_SIZE = 100;

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
                        return new Thread(r,"my-thread" + r);
                    }
                });
        int count = 0;
        while (count < 100) {
            threadPoolExecutor.submit(new Producer(queue, MAX_SIZE));
            try {
                TimeUnit.MICROSECONDS.sleep(5L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadPoolExecutor.submit(new Consumer(queue, MAX_SIZE));
            count++;
        }
        threadPoolExecutor.shutdown();

//        while (true) {
//            new Thread(new Producer(queue, MAX_SIZE)).start();
//            new Thread(new Consumer(queue, MAX_SIZE)).start();
//        }
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
//                try {
//                    TimeUnit.MICROSECONDS.sleep(500L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
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
