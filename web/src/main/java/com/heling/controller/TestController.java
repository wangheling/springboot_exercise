package com.heling.controller;

import com.heling.test.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author whl
 * @description
 * @date 2019/06/26 0:00
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("test")
    public String test() {
        return testService.test();
    }
}
