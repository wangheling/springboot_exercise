package com.heling.juc.study;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: wangheling
 * @Date: 2019/7/3 14:22
 * @Description: 自定义线程工厂：设置线程名，守护线程，优先级以及UncaughtExceptionHandler
 */
@Slf4j
public class MyThreadFactory implements ThreadFactory {

    //test client
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(5, new MyThreadFactory("测试线程"));
        for (int i = 0; i < 10; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("do something.........");
                    //未捕获的异常，走自定义的UncaughtExceptionHandler逻辑
                    int i = 1 / 0;
                }
            });
        }
        pool.shutdown();
    }

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public MyThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-thread-";

    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        //守护线程
        if (t.isDaemon())
            t.setDaemon(true);
        //线程优先级
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);

        /**
         * 处理未捕捉的异常
         */
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("do something to handle uncaughtException");
            }
        });
        return t;
    }

}
