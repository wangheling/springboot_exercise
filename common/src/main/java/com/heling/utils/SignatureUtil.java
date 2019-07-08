//package com.heling.utils;
//
//import lombok.extern.slf4j.Slf4j;
//
//import javax.crypto.Cipher;
//import java.util.*;
//
///**
// * @author whl
// * @description
// * @date 2019/07/06 16:57
// */
//@Slf4j
//public class SignatureUtil {
//
//    public static Boolean validate(Map<String, Object> data, String privateKey) {
//
//        if (null == data || data.size() < 1 || data.get("timestamp") == null) {
//            log.info("sign data is empty or timestamp is null!");
//            return false;
//        }
//        if (!data.containsKey("sign") || data.get("sign") == null) {
//            log.info("sign is null!");
//            return false;
//        }
//        final Map<String, Object> tempData = new HashMap<>(data);
//        tempData.remove("sign");
//        final String newSign = getSign(tempData, privateKey);
//        final String sign = (String) data.get("sign");
//        return sign.equals(newSign);
//    }
//
//
//    private static String getSign(Map<String, Object> data, String privateKey) {
//        List<String> upperParamKeys = new ArrayList<>();//存储参与签名的转换成大写后的key
//        Map<String, Object> signData = new HashMap<>();//存储参与签名的数据
//
//        for (String key : data.keySet()) {
//            Object value = data.get(key);
//            //1. data中的value值为null或者""的栏位不参与签名
//            if (value != null && !"".equals(value.toString())) {
//                String upperParamKey = key.toUpperCase();
//                upperParamKeys.add(upperParamKey);
//                signData.put(upperParamKey, data.get(key));
//            }
//        }
//        //2. 按字典顺序排序
//        Collections.sort(upperParamKeys);
//
//        //3. 将参与签名的数据拼接成字符串
//        final StringBuilder builder = new StringBuilder();
//        for (String upperParamKey : upperParamKeys) {
//            builder.append(upperParamKey).append(signData.get(upperParamKey));
//        }
//        String newSign = RSAUtil.encryptByprivateKey(builder.toString(), privateKey, Cipher.ENCRYPT_MODE);
//        return newSign;
//    }
//}
