package com.heling.utils;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangheling
 * @Date: 2019/7/10 09:54
 * @Description:
 */
public class RSAUtilTest {

    @Test
    public void testGetKeys() throws Exception {
        Map<String, Object> map = RSAUtil.genKeyPair();
        Assert.assertEquals(2, map.size());
    }


    @Test
    public void getKeys() throws Exception {
        //生产密钥A  加密
        Map<String, Object> keyPairA = RSAUtil.genKeyPair();
        String publicKeyA = RSAUtil.getPublicKey(keyPairA);
        String privateKeyA = RSAUtil.getPrivateKey(keyPairA);
        //生成密钥B  签名
        Map<String, Object> keyPairB = RSAUtil.genKeyPair();
        String publicKeyB = RSAUtil.getPublicKey(keyPairB);
        String privateKeyB = RSAUtil.getPrivateKey(keyPairB);

        System.out.println("pulicKeyA:" + publicKeyA);
        System.out.println("privateKeyA:" + privateKeyA);
        System.out.println("publicKeyB:" + publicKeyB);
        System.out.println("privateKeyB:" + privateKeyB);
    }


    @Test
    public void generateSignOfStopCalling() throws Exception {

        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIqUbavy+8m3vm+Q4mv4DtfbQ3iOZfs9LE39Mr+6Pg3nTOmBROsgiZlHdjuNVghjOdE+JkqFalGp1pwAwzD05cvH0dHbEhgW7mnB27vBRZgkSHvYrbPGY0y3jAnyO0UbS9npSBhaLbQ/P7HTbZUE4o+9whuZzZ3iK3oprVhZjvKDAgMBAAECgYBXFjnpohRLStY5RGKWGVgynI9FFq/N+D58cE7RIBhZIm86GsuVu2fdVIRfnk1fjgPKk/T6RedvlHzSosQHYw95U18cY1K2wfXzqcB39C4hS81pGfCfps+rvviRqVg/V7RmxgOiVw2rB+k6Pp54GyKvf5FEbvKIDaJYYRSYCnR4WQJBAOkqSQ7lz+XI14nhFZ0+8pS8QlTsrX0NsNfKvZEAh05Ym+8x+ugeF1WMIsGXMohVqDSkXtJNODNTj+BZkDk3Q8cCQQCYJsfXnzgIZCYFZ/Tl1fenaYjaBouTmXUrjjM5irKpfYRc12N83TVCiWutdAIRkhQLuA1fbGnIz4+c9J9ZmyNlAkEAyhwFSAycsSnu621YNv2heJvXQ5YErCl56ROsTkyO/LKCOEyymRG8WEMovXY6L0NQkpEIQHL/DD6asHXyNiZZcQJBAJV+3TJ+TabpB/ad2yGMp8YFSqK2daPzzw2ZrbknDz67hjdWpnkEPJWAwpzt9C5hcnLFR9Zvhkz5q+kS7hMz/IUCQF3ZE2X4i+V1tYUd4SQXL/6zDWG9tOxtVIisLazH7EGao1eVBOPOZNSxz7umiO4nTqo6rFhkoVY7Pv1EOQph9qc=";
        String cmPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZ2zbKOxchECRCoqSA6EKIGrGt6Wy2+YI2UevnEZkesHtNwcJh/5Mu6fnWSh1nuWaRWWc0FPQ7fJb0gRa5iv4AEQJg+ZNY70mAoTQpSTGwfgNjnMmCfZew7rnv95VncxWhDFMgDlJ6ByF1HfLkdC8f+adkt7OybKy/IJGBolPV1QIDAQAB";
        Map<String, Object> map = new HashMap<>();
        map.put("tenantCode", "9RG");
        List txList = new ArrayList<Map<String, String>>();
        Map<String, String> innerMap01 = new HashMap<>();
        innerMap01.put("type", "NWDHS01");
        innerMap01.put("txNo", "7220161651345413");

        Map<String, String> innerMap02 = new HashMap<>();
        innerMap02.put("txNo", "7220161651345409");
        innerMap02.put("type", "NWDHS01");
        txList.add(innerMap02);
        txList.add(innerMap01);
        map.put("txList", JSONObject.toJSONString(txList));
        Object body = SignatureUtil.generateSignRequestBody(map, cmPublicKey, privateKey);
        System.out.println(body);
//        tenantCode9RGtxList[{"txNo":"7220161651345413","type":"NWDHS01"},{"txNo":"7220161651345409","type":"NWDHS01"}]
    }

}
