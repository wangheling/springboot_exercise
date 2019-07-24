package com.heling.juc.study.part11;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author whl
 * @description SimpleDateFormat线程不安全,以及解决方案比较
 * @date 2019/07/24 20:28
 */
public class Demo01 {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 报错信息：NumberFormatException，课件sdf不是线程安全的，当多线程情况下共用一个sdf会出现
     * 线程不安全。
     * Exception in thread "Thread-2253" java.lang.NumberFormatException: multiple points
     * 	at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:1890)
     * 	at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
     * 	at java.lang.Double.parseDouble(Double.java:538)
     * 	at java.text.DigitList.getDouble(DigitList.java:169)
     * 	at java.text.DecimalFormat.parse(DecimalFormat.java:2056)
     * 	at java.text.SimpleDateFormat.subParse(SimpleDateFormat.java:1869)
     * 	at java.text.SimpleDateFormat.parse(SimpleDateFormat.java:1514)
     * 	at java.text.DateFormat.parse(DateFormat.java:364)
     * 	at com.heling.juc.study.part11.Demo01.lambda$main$0(Demo01.java:20)
     * 	at java.lang.Thread.run(Thread.java:745)
     * Exception in thread "Thread-2263" java.lang.NumberFormatException: For input string: "2019E20194"
     * 	at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
     * 	at java.lang.Long.parseLong(Long.java:589)
     * 	at java.lang.Long.parseLong(Long.java:631)
     * 	at java.text.DigitList.getLong(DigitList.java:195)
     * 	at java.text.DecimalFormat.parse(DecimalFormat.java:2051)
     * 	at java.text.SimpleDateFormat.subParse(SimpleDateFormat.java:1869)
     * 	at java.text.SimpleDateFormat.parse(SimpleDateFormat.java:1514)
     * 	at java.text.DateFormat.parse(DateFormat.java:364)
     * 	at com.heling.juc.study.part11.Demo01.lambda$main$0(Demo01.java:20)
     * 	at java.lang.Thread.run(Thread.java:745)
     * Exception in thread "Thread-2285" java.lang.NumberFormatException: For input string: ""
     */
//    public static void main(String[] args) {
//        while (true) {
//            Thread thread = new Thread(() -> {
//                try {
//                    System.out.println(sdf.parse("2019-07-24"));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            });
//            thread.start();
//        }
//    }

    /**
     * 原因：
     *   parse方法里的establish方法里有下面逻辑：
     *   cal.clear();
     *   cal.setXXX();
     *   return cal;
     *   这三个步骤不是原子性，存在安全问题。
     * 解决：
     * 1.每个线程new一个新的sdf，但是造成大量垃圾；
     * 2.parse进行同步，存在锁竞争，效率低下；
     * 3.ThreadLocal，下面是代码：
     */
    static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public static void main(String[] args) {

        while (true) {
            new Thread(()-> {
                try {
                    System.out.println(local.get().parse("2019-07-24"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    local.remove();
                }
            }).start();
        }
    }
}
