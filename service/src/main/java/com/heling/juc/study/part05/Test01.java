package com.heling.juc.study.part05;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author whl
 * @description CopyOnWriteArrayList 和 ArrayList在并发情况下的安全性测试
 * 结论：3000并发情况下后者可能size<3000,而前者永远都是3000，所以前者是线程安全的
 * CopyOnWriteArrayList底层原理：add（e）时候，会先加锁，会复制一个length + 1的新数组，将元素复制过去，最后一个设置为e,
 * 最后新数组替换掉原来的就数组，最后释放锁。
 * <p>
 * 为什么锁保证了安全性，为啥还要每次复制呢？？？
 *     旧数组（副本）读不会阻塞，写阻塞，相当于读写分离，既保障读的效率又保障的线程安全
 * 适用于读操作远大于写操作的场景。
 * @date 2019/07/11 20:19
 */
public class Test01 {

    private final static int threads = 3000;

    private static final CyclicBarrier barrier = new CyclicBarrier(threads);

    private static final CountDownLatch latch = new CountDownLatch(threads);

    public static void main(String[] args) throws InterruptedException {

        ArrayList<String> arrayList = new ArrayList<>();
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
//                    arrayList.add("test");
                    cowList.add("test");
                    latch.countDown();
                }
            });
            thread.start();
        }
        latch.await();

        //the end,arryList size =2998
        System.out.println("the end,arryList size =" + arrayList.size());
        //the end,cowList size =3000
        System.out.println("the end,cowList size =" + cowList.size());
    }
}
