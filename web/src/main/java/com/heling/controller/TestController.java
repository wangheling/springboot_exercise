package com.heling.controller;

import com.heling.test.TestService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

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

    @PostMapping("test")
    public String test(@RequestBody Map<String, Object> map) {
        return testService.test(((Integer) map.get("id")));
    }
}
