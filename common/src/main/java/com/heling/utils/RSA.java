package com.heling.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.val;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * <p>RSA签名,加解密处理核心文件，注意：密钥长度1024</p>
 */
public class RSA {

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "publicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "privateKey";

    /**
     * <p>
     * 生成密钥
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }

    /**
     * 签名字符串
     *
     * @param text       要签名的字符串
     * @param privateKey 私钥(BASE64编码)
     * @param charset    编码格式
     * @return 签名结果(BASE64编码)
     */
    public static String sign(String text, String privateKey, String charset) throws Exception {

        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(getContentBytes(text, charset));
        byte[] result = signature.sign();
        return Base64.encodeBase64String(result);

    }

    /**
     * 签名字符串
     *
     * @param text      要签名的字符串
     * @param sign      客户签名结果
     * @param publicKey 公钥(BASE64编码)
     * @param charset   编码格式
     * @return 验签结果
     */
    public static boolean verify(String text, String sign, String publicKey, String charset)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(getContentBytes(text, charset));
        return signature.verify(Base64.decodeBase64(sign));


    }

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解�?
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
//        return Base64.encodeBase64String(decryptedData);

    }

    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
//        return Base64.encodeBase64String(decryptedData);

    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
//        return Base64.encodeBase64String(encryptedData);

    }

    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
//        return Base64.encodeBase64String(encryptedData);

    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

//    //    把byte[]元素之间添加空格，并转化成字符串返回，
//    public static String byteToString(byte[] resouce){
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < resouce.length; i++) {
//            if (i == resouce.length-1) {
//                sb.append(Byte.toString(resouce[i]));
//            }else{
//                sb.append(Byte.toString(resouce[i]));
//                sb.append(" ");
//            }
//        }
//        return sb.toString();
//    }
//
//    //    把字符串按照空格进行拆分成数组，然后转化成byte[],返回
//    public static byte[] stringToByte(String resouce){
//        String[] strArr = resouce.split(" ");
//        int len = strArr.length;
//        byte[] clone = new byte[len];
//        for (int i = 0; i < len; i++) {
//            clone[i] = Byte.parseByte(strArr[i]);
//        }
//        return clone;
//    }


    public static void main(String[] args) throws Exception {

        Map<String, Object> map = genKeyPair();
        System.out.println(getPublicKey(map));
        System.out.println(getPrivateKey(map));


//        //商户
        String privateKeyB = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbxq5HkzlIe+rvvKP9EtG9IK3asTlFwV8GHzDwbhonujzRoPIniWGnXyh28s31PmBFayURx2yFF/WXmVE0c1zX/SASZRB7rCNzSnUTnZxI5G5dx64IvHYigmnBCkSrmff1aDgamA2bHCf54ADVVnC8xXekubrShALOQWy6caUdhAgMBAAECgYACyNiKa3cm6q1jdCkEdIcDmog6uzkCpZjo2BF7dmKH/t+jbtzXClbpNPLkk4uYHzTF9S/LVHJfbcQb8iDR/1s0+2S0wBlXmi38G131tOMH9IHbmj9CMipT4qIQ2QHuZvcrvROcF4YRujsYukUcTMll68PT8N60nsjY6iFn17OeMQJBAPH2ORyd/eeEv2jlQcn4b/BZD3t0OBw+LI9iOBQVYwkEGdttUSzmGCbeX5Mv8mxBxGGAWfU51CU8skXp1nsYdo0CQQCfs5c6JC4+QccofLU5pls8Tnix49Ii2rDGD3ZrTRwpXw9GBJ3w7Zx7/8xXZuc/Yry5DNdH5E9VjXA//9rBFfklAkBvnb3KzbcKzdnecaNfoBNDNFJICNy5apTzr8NewF7nEdU7u7nulFYf2AUOyNdzyQLmV2coEH0kkFNMnQorzqUhAkAED+ph1TfD56JsOjfV3pBliU1yoOvgbRWgBBasBgsYVk2qJ0XpMfIQCWKmOnzZVOaeAmCbeIg4Ed/wEM78qFVtAkEAubD0Z+MMHSYeYPSQD7vfWzyf3bOP9oTs/iKmCOePU550zOVkFy6t4b4mQNY+Gc4Q2SqAq9jTJNxdKb41KV+TeQ==";
        String publicKeyA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClr3zmhn2CrNxq01WvwOPeCsXe2+yTN7aZ2v3pLCrTcplf5ZcQiLB1jukeS/KY940pf43sHjYdYkzuHk6dEWdNJs9IFIFsLwtKHOZMuTPLajDSnUgc2gmPDm1Q/1Mb0g4mDBs8t1RN2ejn0T+YEji8zT34oecWY6CdU2NX95qPHwIDAQAB";

//        //平台
        String publicKeyB = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW8auR5M5SHvq77yj/RLRvSCt2rE5RcFfBh8w8G4aJ7o80aDyJ4lhp18odvLN9T5gRWslEcdshRf1l5lRNHNc1/0gEmUQe6wjc0p1E52cSORuXceuCLx2IoJpwQpEq5n39Wg4GpgNmxwn+eAA1VZwvMV3pLm60oQCzkFsunGlHYQIDAQAB";
        String privateKeyA = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKWvfOaGfYKs3GrTVa/A494Kxd7b7JM3tpna/eksKtNymV/llxCIsHWO6R5L8pj3jSl/jeweNh1iTO4eTp0RZ00mz0gUgWwvC0oc5ky5M8tqMNKdSBzaCY8ObVD/UxvSDiYMGzy3VE3Z6OfRP5gSOLzNPfih5xZjoJ1TY1f3mo8fAgMBAAECgYBREbvYhCf3f6NeNcfwdj5I0BafhBaOti0HvSqOJAlUavd2/7zZ3zgQXXvdUiYDCOWuT3Ze+S1bVVT1Eik/G1vij5arsl0Gb51tvLoOPf2i5LKNF7ItaZPgWPKmrGZIRpM60lReIInpAmeanqbqA0KYspJ1xJohNYC9ulrZAYTGyQJBANVOnaJg6VrilFwbpc6A2bWUWrN3FUxXRaEdVVmRXJOI8J4wQ3quoGcfXFQj5hixwiE/UKho6N9DluF5YyGN1R0CQQDG2NjN8eoKHn8dP2hmOa/dXMFnzntii2KHIWEtslFlNhdYCL9h/iDBpjXuBwmCkkq1/x3QuyXM1gam2rzEP6xrAkAzVllwBD0Fh7THOkw8mBxNrGfr+4QfvH1gmmu5Q9fxFis7F0iWVUL7rruM8JrnZJjhYZiq/5zUZzMADzQBll4tAkBDviaGau8XyXH2zd5MTUTbvjjpm4+vb9r+PGH2BnTybS4gwMIqqUARmQVwdRdJU3t+a06GCOwEz7luaMC8fxDHAkEAvBsIxSTPmVkr0ubPweFxPqvgy/gz6BsLIo23rjgeFiuIxl778Q/3FPiLp7ObpRIFKjeOo0aqaDGpxn7cNeYLzw==";

        String content = "王鹤翎";


        //商户发送请求
        //先用私钥B生成签名sign
//        String sign =  Base64.encodeBase64String(encryptByPrivateKey(content.getBytes(), privateKeyB));
        //使用公钥A对content和sign进行加密
//        Map<String, String> map = new HashMap<>();
//        map.put("data", content);
//        map.put("sign", sign);
//        System.out.println(map.toString());
//        String request =  Base64.encodeBase64String(encryptByPublicKey(map.toString().getBytes(), publicKeyA));
        String request =  Base64.encodeBase64String(encryptByPublicKey(content.getBytes(), publicKeyA));
        System.out.println("商户发送请求:" + request);

//        ==============================================================================

        //平台接收请求进行解密验签
        //使用私钥A解密
        String s =  Base64.encodeBase64String(decryptByPrivateKey(request.getBytes(), privateKeyA));
        System.out.println(s);
//        Map map2 = JSONObject.parseObject(s, Map.class);
//        String content2 = map.get("data");
//        System.out.println("商户发送的content：" + content2);
//        System.out.println("=======================验签开始==========================");

//        String oldSign = (String) map2.get("sign");
//
//        //用公钥B生成新签名
//        String newSign =  Base64.encodeBase64String(encryptByPublicKey(content2.getBytes(), publicKeyB));
//
//        if (oldSign.equals(newSign)) {
//            System.out.println("验签成功");
//        } else {
//            System.out.println("验签失败");
//        }

    }
}
