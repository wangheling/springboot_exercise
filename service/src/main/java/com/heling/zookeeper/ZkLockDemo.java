package com.heling.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Author: wangheling
 * @Date: 2019/8/14 14:43
 * @Description: curator实现zk分布式锁
 */
public class ZkLockDemo {

    private static final String ZK_IP_ADDRESSES = "";

    public static void main(String[] args) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(ZK_IP_ADDRESSES).
                sessionTimeoutMs(1000).
                retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        curatorFramework.start();
        final InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/locks");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "->尝试获取锁");
                try {
                    lock.acquire();
                    System.out.println(Thread.currentThread().getName() + "->获得锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(4000);
                    lock.release();
                    System.out.println(Thread.currentThread().getName() + "->释放锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "t" + i).start();
        }
    }
}
