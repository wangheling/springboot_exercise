package com.heling.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.heling.filter.BodyReaderRequestWrapper;
import com.heling.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author whl
 * @description 验证签名拦截器
 * @date 2019/07/06 16:07
 */
@Slf4j
public class ValidateSignInterceptor implements HandlerInterceptor {

    @Value("${key.publicKeyA}")
    private String publicKeyA;

//    @Value("${key.privateKeyB}")
//    private String privateKeyB;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("securityInterceptor works");
        if (request instanceof BodyReaderRequestWrapper) {
            BodyReaderRequestWrapper tempHttpServletRquest = (BodyReaderRequestWrapper) request;
            String body = tempHttpServletRquest.getBody();
            final Map map = (Map) JSONObject.parseObject(body);
            //验签
            boolean isSuccess = validateSign(map);
            return isSuccess;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * @param map
     * @return
     * @throws Exception
     */
    private boolean validateSign(Map map) throws Exception {

        if (null == map || map.size() < 1) {
            return false;
        }

        if (!map.containsKey("sign") || map.get("sign") == null) {
            return false;
        }

        String sign = (String) map.get("sign");

        Map tempMap = new HashMap<>(map);

        tempMap.remove("sign");

        List<String> upperParamKeys = new ArrayList<>();//存储参与签名的转换成大写后的key
        Map<String, Object> signData = new HashMap<>();//存储参与签名的数据

        for (Object key : tempMap.keySet()) {
            Object value = tempMap.get((String) key);
            //1. data中的value值为null或者""的栏位不参与签名
            if (value != null && !"".equals(value.toString())) {
                String upperParamKey = ((String) key).toUpperCase();
                upperParamKeys.add(upperParamKey);
                signData.put(upperParamKey, tempMap.get(key));
            }
        }
        //2. 按字典顺序排序
        Collections.sort(upperParamKeys);

        //3. 将参与签名的数据拼接成字符串
        final StringBuilder sb = new StringBuilder();
        for (String upperParamKey : upperParamKeys) {
            sb.append(upperParamKey).append(signData.get(upperParamKey));
        }
        boolean verify = RSAUtil.verify(sb.toString(), sign, publicKeyA);
        return verify;

    }

}
