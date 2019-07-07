package com.heling.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whl
 * @description
 * @date 2019/07/07 10:59
 */
public class PassUtil {

    public static String publicKey; // 公钥
    public static String privateKey; // 私钥

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 53;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 64;

    /**
     * 生成公钥和私钥
     */
    public static void generateKey() {
        // 1.初始化秘钥
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom sr = new SecureRandom(); // 随机数生成器
            keyPairGenerator.initialize(512, sr); // 设置512位长的秘钥
            KeyPair keyPair = keyPairGenerator.generateKeyPair(); // 开始创建
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 进行转码
            publicKey = Base64.encodeBase64String(rsaPublicKey.getEncoded());
            // 进行转码
            privateKey = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 私钥匙加密或解密
     *
     * @param content
     * @param privateKeyStr
     * @return
     */
    public static String encryptByprivateKey(String content, String privateKeyStr, int opmode) {
        // 私钥要用PKCS8进行处理
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyStr));
        KeyFactory keyFactory;
        PrivateKey privateKey;
        Cipher cipher;
        byte[] result;
        String text = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            // 还原Key对象
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(opmode, privateKey);
            if (opmode == Cipher.ENCRYPT_MODE) { // 加密
                result = cipher.doFinal(content.getBytes());
                text = Base64.encodeBase64String(result);
            } else if (opmode == Cipher.DECRYPT_MODE) { // 解密
                result = cipher.doFinal(Base64.decodeBase64(content));
                text = new String(result, "UTF-8");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 公钥匙加密或解密
     *
     * @param content
     * @param privateKeyStr
     * @return
     */
    public static String encryptByPublicKey(String content, String publicKeyStr, int opmode) {
        // 公钥要用X509进行处理
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
        KeyFactory keyFactory;
        PublicKey publicKey;
        Cipher cipher;
        byte[] result;
        String text = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            // 还原Key对象
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(opmode, publicKey);
            if (opmode == Cipher.ENCRYPT_MODE) { // 加密
                result = cipher.doFinal(content.getBytes());
                text = Base64.encodeBase64String(result);
            } else if (opmode == Cipher.DECRYPT_MODE) { // 解密
                result = cipher.doFinal(Base64.decodeBase64(content));
                text = new String(result, "UTF-8");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    // 测试方法
    public static void main(String[] args) {
        /**
         * 注意： 私钥加密必须公钥解密 公钥加密必须私钥解密
         */
//        System.out.println("-------------生成两对秘钥，分别发送方和接收方保管-------------");
//        PassUtil.generateKey();
//        System.out.println("公钥匙给接收方:" + PassUtil.publicKey);
//        System.out.println("私钥给发送方:" + PassUtil.privateKey);
//
//        System.out.println("-------------第一个栗子，私钥加密公钥解密-------------");
        // String textsr = "早啊，你吃早饭了吗？O(∩_∩)O~";
        // // 私钥加密
        // String cipherText = PassUtil.encryptByprivateKey(textsr,
        // PassUtil.privateKey, Cipher.ENCRYPT_MODE);
        // System.out.println("发送方用私钥加密后：" + cipherText);
        // // 公钥解密
        // String text = PassUtil.encryptByPublicKey(cipherText,
        // PassUtil.publicKey, Cipher.DECRYPT_MODE);
        // System.out.println("接收方用公钥解密后：" + text);

//        System.out.println("-------------第二个栗子，公钥加密私钥解密-------------");
//        // 公钥加密
//        String textsr = "吃过啦！你吃了吗？O(∩_∩)O~";
//
//        String cipherText = PassUtil.encryptByPublicKey(textsr, PassUtil.publicKey, Cipher.ENCRYPT_MODE);
//        System.out.println("接收方用公钥加密后：" + cipherText);
//        // 私钥解密
//        String text = PassUtil.encryptByprivateKey(cipherText, PassUtil.privateKey, Cipher.DECRYPT_MODE);
//        System.out.print("发送方用私钥解密后：" + text);


        //商户
        String publicKeyA ="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK5vNSRbD4i3J9JV+R5GBzwknD69sxUj4f2Zv86czRCnF8rMMduIhGSuJ6MD+zPDYxiIRlo5Sr93axnmW1oWnUECAwEAAQ==";
        String privateKeyB = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAvrAar1bXzAozy7LnZ1uBlsKLdummP4qIb/mfwGEjQbMytgsX1ColboFFCFzXeJMvXiScbT7sSSVMJv2F8tMhswIDAQABAkAjvGkj1wiWKotOb3bCffmpQUhVvq/zVIkBQyzXJt0uoI2mo7VSTak41YTkZaSR7WX+CtZbcmhNHI6IibHMO3xxAiEA7YW0k1I0XqqtxvtOk7Qhro9HNgZn/llJeKIGYraM1yUCIQDNha9FJKTfLueXW1UyJIhZbOy7hUv96cEnaZ0riF5J9wIgNju1Q34dnw/xpeFzy53xot3sMJW94QGaGQVF1/w+pFECIQDB5nmZC1x9sFwRQbscZrk5XXnv5W/vrAVu8Ma3Fp6WuwIhANiTPCOsGQf08Qqw5UyqMIe/omWJ9wiHJ8Aghx+PznbM";

        //平台
        String privateKeyA="MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEArm81JFsPiLcn0lX5HkYHPCScPr2zFSPh/Zm/zpzNEKcXyswx24iEZK4nowP7M8NjGIhGWjlKv3drGeZbWhadQQIDAQABAkAQ8PILVuC6WXd/oSPxwcFgIPofydLDwdloBO+xBF+SN08v7LrTImKlpOej3SyM3SRNNkx/srolMrVZninCB1QBAiEA20rc5xqj8sLEzxDzbonmA9C9QEXHWUTIDT96ei/A8aECIQDLohW2Tp6YTst9yIeqEBN2HHO3xZCOlNV5G/WT2ttHoQIgF8mOHs+H+zxfZuq6zUHOHpnU/ZTUI/3PCYjaO0jU0CECIBmiS81pVDmkIaWx6rIT1/9UUZTev/XS8pSOWnnEYTpBAiEAihHxdU7yF8ulUuARWLAnse3G83VUz1X31kB9J39RcFI=";
        String publicKeyB = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKJdNqHAjF4u196k7E5Chf85T2YTZZ4HhL5vznwoUBNhPAKy3p2lJGIG3ZYzz4PvD3THq6WvKHdzcgiBHjNYNCkCAwEAAQ==";

        String content = "王鹤翎";


        //商户发送请求
        //先用私钥B生成签名sign
        String sign = encryptByprivateKey(content, privateKeyB, Cipher.ENCRYPT_MODE);
//        String sign = "hhahahah";
        //使用公钥A对content和sign进行加密
        Map<String, String> map = new HashMap<>();
        map.put("data", content);
        map.put("sign", sign);
        String request = PassUtil.encryptByPublicKey(JSONObject.toJSONString(map), publicKeyA, Cipher.ENCRYPT_MODE);

        System.out.println("商户发送请求:" + map);

//        ==============================================================================

        //平台接收请求进行解密验签
        //使用私钥A解密
        String s = PassUtil.encryptByprivateKey(request, privateKeyA, Cipher.DECRYPT_MODE);
        Map map2 = JSONObject.parseObject(s, Map.class);
        String content2 = (String) map2.get("data");
        System.out.println("商户发送的content：" + content2);
        System.out.println("=======================验签开始==========================");

        String oldSign = (String) map2.get("sign");

        //用公钥B生成新签名
        String newSign = encryptByPublicKey(content2, publicKeyB, Cipher.ENCRYPT_MODE);

        if (oldSign.equals(newSign)) {
            System.out.println("验签成功");
        } else {
            System.out.println("验签失败");
        }

    }
}
