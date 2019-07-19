package com.heling.juc.study;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: wangheling
 * @Date: 2019/7/19 13:52
 * @Description: 简易版非公平可重入锁
 * <p>
 * 思路：
 * 当获取锁的时候，先try，看state是否大于0，如果等于0则资源是空闲状态，则cas争夺锁，如果失败则放入同步对了；
 * 如果state大于0，则看是否当前持有锁的线程是否是自己，是则获取成功，state + 1；
 * 如果不是自己持有，则try失败；
 * <p>
 * 根据上面try的true or false，false的话则cas放入到同步队列的tail。
 * <p>
 * 释放锁：
 * 因为是同步状态，所以无需cas，如果stae -1 > 0则释放失败，否则释放成功。
 */
public class MyReentrantNonFairLock implements Lock, Serializable {

    final private Sync sync;

    /**
     * 定义Sync继承AQS
     */
    static class Sync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            //当前state值
            int state = getState();

            final Thread currentThread = Thread.currentThread();

            if (state == 0) {
                //资源空闲，竞争锁
                if (compareAndSetState(0, arg)) {
                    setExclusiveOwnerThread(currentThread);
                    return true;
                }
            } else {
                if (currentThread == getExclusiveOwnerThread()) {
                    //自己是锁的持有者，则state + 1
                    final int nextState = state + arg;
                    //是同步状态，无需cas
//                    compareAndSetState(nextState, state);
                    setState(nextState);
                    return true;
                }
            }
            return false;
        }


        @Override
        protected boolean tryRelease(int arg) {

            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            int state = getState();

            final int newState = state - arg;

            boolean success = false;

            if (newState == 0) {
                success = true;
                setExclusiveOwnerThread(null);
            }
            setState(newState);
            return success;
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }
    }


    public MyReentrantNonFairLock() {
        this.sync = new Sync();
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


}
