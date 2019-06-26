package com.heling.test.impl;

import com.alibaba.fastjson.JSONObject;
import com.heling.dao.Test;
import com.heling.mapper.TestMapper;
import com.heling.test.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author whl
 * @description
 * @date 2019/06/26 0:02
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService {

    @Resource
    private TestMapper testMapper;

    @Override
    public String test() {
        Test test = testMapper.selectByPrimaryKey(1);
        log.info("响应结果为:{}",JSONObject.toJSON(test));
        return JSONObject.toJSONString(test);
    }
}
