package com.heling.juc.study.part04;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author whl
 * @description:通过两个线程分别计算两个数组里面“0”的个数，最后汇总 使用原子类的cas非阻塞算法统计
 * @date 2019/07/10 21:34
 */
public class Test01 {

    //定义原子类计数器
    private static AtomicLong count = new AtomicLong(0);

    private final static Integer[] arr_one = new Integer[]{0, 2, 5, 0, 5, 8, 0, 0, 10, 0, 9};
    private final static Integer[] arr_two = new Integer[]{1, 2, 5, 0, 5, 8, 0, 0, 10, 0, 0, 0, 10};

    public static void main(String[] args) throws InterruptedException {
        //统计arr_one中0的个数
        Thread thread01 = new Thread(() -> {
            for (int v : arr_one) {
                if (v == 0) {
                    count.incrementAndGet();
                }
            }
        });

        //统计arr_two中0的个数
        Thread thread02 = new Thread(() -> {
            for (int v : arr_two) {
                if (v == 0) {
                    count.incrementAndGet();
                }
            }
        });

        thread01.start();
        thread02.start();
        //等待两个工作线程执行完毕
        thread01.join();
        thread02.join();
        long result = count.get();
        System.out.println("统计出0的个数：" + result);
    }


}
