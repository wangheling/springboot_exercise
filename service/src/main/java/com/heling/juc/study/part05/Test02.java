package com.heling.juc.study.part05;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author whl
 * @description CopyOnWriteArrayList的弱一致性
 * @date 2019/07/11 21:25
 */
public class Test02 {

    public static void main(String[] args) throws InterruptedException {
        /**
         * 报错：ConcurrentModificationException
         */
//        ArrayList<Integer> list = new ArrayList<>();

        /**
         * print result：1 1 1 1 1
         * 在写操作前获取迭代器，得到的是副本，遍历的事副本，另一个线程修改list，导致了弱一致性
         */
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                list.set(0, 2);
                list.remove(4);
            }
        });

        Iterator<Integer> iterator = list.iterator();
        //在获取迭代器之后开启线程
        thread.start();
        thread.join();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
    }
}
