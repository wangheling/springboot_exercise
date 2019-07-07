package com.heling.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.heling.utils.BodyReaderHttpServletRequestWrapper;
import com.heling.utils.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whl
 * @description
 * @date 2019/07/06 16:07
 */
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

    @Value("${key.publicKey}")
    private String publicKey;

    @Value("${key.privateKey}")
    private String privateKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("securityInterceptor works");
        if (request instanceof BodyReaderHttpServletRequestWrapper) {
            BodyReaderHttpServletRequestWrapper tempHttpServletRequest = (BodyReaderHttpServletRequestWrapper) request;
            String body = tempHttpServletRequest.getBody();
            final Map<String, Object> requestDataMap = JSONObject.parseObject(body);
            boolean signResult = SignatureUtil.validate(requestDataMap, privateKey);
            if (!signResult) { //签名校验失败
                log.info("签名校验失败!");
                throw new RuntimeException("签名不合法");
            }
            return true;
        }

        String contentType = request.getContentType();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
