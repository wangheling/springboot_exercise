package com.heling.juc.study.part02;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author whl
 * @description
 * @date 2019/07/09 21:33
 */
public class Test02 implements Runnable {

    int count = 100;

    @Override
    public void run() {
        while (count > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (Test02.class) {
                System.out.println(count);
                count--;
            }


        }
    }


    public static void main(String[] args) {
        Test02 test02 = new Test02();
        new Thread(test02).start();
        new Thread(test02).start();
    }
}
