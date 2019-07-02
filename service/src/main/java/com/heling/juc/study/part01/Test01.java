package com.heling.juc.study.part01;

import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author whl
 * @description 使用wait、notify实现生产者消费者demo
 * @date 2019/07/02 21:31
 */
@Slf4j
public class Test01 {

    private static final int MAX_SIZE = 10;

    public static void main(String[] args) {

        //阻塞队列
        Queue<String> queue = new ArrayBlockingQueue<>(MAX_SIZE);
        //生产者线程
        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (queue) {
                        while (queue.size() == MAX_SIZE) {
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
        });

        //消费者线程
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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
        });

        producer.start();
        consumer.start();
    }
}