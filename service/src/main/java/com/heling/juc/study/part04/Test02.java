package com.heling.juc.study.part04;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongBinaryOperator;

/**
 * @author whl
 * @description LongAccumulator & LongAdder(是前者的特例)
 * 把一个变量分成多个变量，从而减少多线程竞争，最后进行汇总
 * @date 2019/07/10 22:02
 */
public class Test02 {

    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();//不能设置初始化值
        longAdder.reset();
        longAdder.add(10);
        longAdder.increment();
        long sum = longAdder.sum();
        System.out.println(sum);

        LongAccumulator longAccumulator = new LongAccumulator(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                //可以自定义运算器
//                return left * right;
                return left + right;
            }
        }, 10);//可以设置初始值（10位初始值）
        longAccumulator.accumulate(20);
        longAccumulator.accumulate(30);
        System.out.println(longAccumulator.get());

    }
}
