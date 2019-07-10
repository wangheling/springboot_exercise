package com.heling.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @Auther: wangheling
 * @Date: 2019/7/10 09:54
 * @Description:
 */
public class RSAUtilTest {

    @Test
    public void testGetKeys() throws Exception {
        Map<String, Object> map = RSAUtil.genKeyPair();
        Assert.assertEquals(2, map.size());
    }

}
