//package com.heling.config;
//
//import com.heling.interceptor.SecurityInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @author whl
// * @description 拦截器配置
// * @date 2019/07/06 16:01
// */
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Bean
//    SecurityInterceptor securityInterceptor() {
//        return new SecurityInterceptor();
//    }
//
//
//    @Override
//
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(securityInterceptor())
//                .addPathPatterns("/**");
//    }
//
//}
