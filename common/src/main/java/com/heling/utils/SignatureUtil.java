package com.heling.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @Auther: wangheling
 * @Date: 2019/7/9 15:20
 * @Description: 验签
 */
public class SignatureUtil {

    /**
     * 验签
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static boolean validateSign(Map<String, Object> map, String publicKey) throws Exception {

        if (null == map || map.size() < 1) {
            return false;
        }

        if (!map.containsKey("sign") || map.get("sign") == null) {
            return false;
        }

        String sign = (String) map.get("sign");

        Map<String, Object> tempMap = new HashMap<>(map);

        tempMap.remove("sign");

        List<String> paramKeys = new ArrayList<>();//存储参与签名的转换的key
        Map<String, Object> signData = new HashMap<>();//存储参与签名的数据

        for (String key : tempMap.keySet()) {
            Object value = tempMap.get(key);
            //1. data中的value值为null或者""的栏位不参与签名
            if (value != null && !"".equals(value.toString())) {
                paramKeys.add(key);
                signData.put(key, tempMap.get(key));
            }
        }
        //2. 按字典顺序排序
        Collections.sort(paramKeys);

        //3. 将参与签名的数据拼接成字符串
        StringBuilder sb = new StringBuilder();
        for (String paramKey : paramKeys) {
            sb.append(paramKey).append(signData.get(paramKey));
        }
        boolean result = RSAUtil.verify(sb.toString(), sign, publicKey.trim());
        return result;
    }


    /**
     * 构造sign请求参数requetbody
     *
     * @param requestParams 业务参数
     * @param publicKey     平台公钥
     * @param privateKey    商户私钥
     * @return
     */
    public static Object generateSignRequestBody(Map<String, Object> requestParams,
                                                 final String publicKey,
                                                 final String privateKey) throws Exception {
        Object code = requestParams.get("tenantCode");
        if (null == code) {
            throw new NullPointerException("tenantCode不能为空");
        }
        String tenantCode = (String) code;
        //加签
        List<String> paramKeys = new ArrayList<>();//存储参与签名的的key
        Map<String, Object> signData = new HashMap<>();//存储参与签名的数据
        Set<Map.Entry<String, Object>> entries = requestParams.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            if (null != entry.getValue() && !"".equals(entry.getValue().toString())) {
                paramKeys.add(entry.getKey());
                signData.put(entry.getKey(), entry.getValue());
            }
        }
        //按字典顺序排序
        Collections.sort(paramKeys);

        //生成签名
        StringBuilder sb = new StringBuilder();
        for (String paramKey : paramKeys) {
            sb.append(paramKey).append(JSONObject.toJSON(signData.get(paramKey)));
        }
        String sign = RSAUtil.sign(sb.toString(), privateKey);
        requestParams.put("sign", sign);
        String dataContent = RSAUtil.encryptByPublicKey(JSONObject.toJSONString(requestParams), publicKey);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tenantCode", tenantCode);
        requestBody.put("dataContent", dataContent);
        return JSONObject.toJSON(requestBody);
    }
}
