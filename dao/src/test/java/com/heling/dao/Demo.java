package com.heling.dao;

import com.alibaba.fastjson.JSONObject;
import com.heling.mapper.TestMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author whl
 * @description
 * @date 2019/06/26 17:19
 */
public class Demo {

    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream("F://mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = factory.openSession();
        TestMapper mapper = sqlSession.getMapper(TestMapper.class);
        Test test = mapper.selectByPrimaryKey(1);
        System.out.println(JSONObject.toJSONString(test));

    }
}
