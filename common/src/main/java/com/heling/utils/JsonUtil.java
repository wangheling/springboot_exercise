/*
 * -------------------------------------------------------------------------------------
 * Mi-Me Confidential
 *
 * Copyright (C) 2015 Shanghai Mi-Me Financial Information Service Co., Ltd.
 * All rights reserved.
 *
 * No part of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior
 * written permission of Shanghai Mi-Me Financial Information Service Co., Ltd.
 * -------------------------------------------------------------------------------------
 */
package com.heling.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Json串处理工具类
 *
 * 注：
 *  1. JSONObject本身就是实现了Map<String, Object>接口，可以直接类型转换为 Map
 *  2.
 *
 * Version     : 1.0
 * Created By  : nicholas
 * Created Date: 2016/3/7
 */
public class JsonUtil {

    //默认的序列化特性
    private static final SerializerFeature[] DEFAULT_S_FEATURES = new SerializerFeature[]{
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.SortField
    };

    private static final SerializerFeature[] PRETTY_S_FEATURES = new SerializerFeature[]{
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.SortField,
            SerializerFeature.PrettyFormat
    };

    //默认的解析特性
    private static final Feature[] DEFAULT_P_FEATURES = new Feature[]{
            Feature.OrderedField
    };

    /**
     * 将对象转换为Json字符串，默认按照字段排序
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj, DEFAULT_S_FEATURES);
    }

    /**
     * 将对象转换为特定Feature的Json字符串
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj, SerializerFeature... features){
        return JSON.toJSONString(obj, features);
    }

    /**
     * 将Json串以更直观的方式进行展示
     *
     * <pre>
     *  如显示Map<String, Object>, Value放入字符串，List<Object>
     *  {
     *      "B":"B-DESC",
     *      "C":"C-DESC",
     *      "A":"A-DESC",
     *      "list":[
     *          "a",
     *          {
     *              "id":"1",
     *              "name":"nicholas"
     *          },
     *          [1,2,3,4]
     *      ]
     *  }
     * </pre>
     * @param object
     * @return
     */
    public static String toPrettyJson(Object object) {
        return JSON.toJSONString(object, PRETTY_S_FEATURES);
    }

    /**
     * 将json格式字符串转换为JSONObject对象
     * @param jsonStr
     * @return
     */
    public static JSONObject parseJsonObject(String jsonStr){
        return JSON.parseObject(jsonStr, DEFAULT_P_FEATURES);
    }

    /**
     * 根据JSON串的Key获取Value值
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static <T> T parseValue(JSONObject jsonObject, String key, Class<T> clazz){
        if (jsonObject != null) {
            T value = jsonObject.getObject(key, clazz);
            return value;
        }

        return null;
    }

    /**
     * 将JSON串解析为对象
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonStr, Class<T> clazz){
        return JSON.parseObject(jsonStr, clazz, DEFAULT_P_FEATURES);
    }

    /**
     * 将Json串转换为 List
     *
     * @param jsonStr   JSON字符串
     * @param clazz     泛型
     * @param <T>
     * @return
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> clazz) {
        return JSON.parseArray(jsonStr, clazz);
    }

    public static <T> List<T> parseList(Object jsonObject, Class<T> clazz) {
        String jsonStr = JsonUtil.toJsonString(jsonObject);
        return JsonUtil.parseList(jsonStr, clazz);
    }

    /**
     * 将Json串转换为 Map, 并保证 Map的顺序与Json串顺序一致
     *
     *
     * <pre>
     *   参考<code>JSONObject</code>的构造方法, 底层实现的数据结构选择 LinkedHashMap
     *
     *   public JSONObject(int initialCapacity, boolean ordered) {
     *        if(ordered) {
     *          this.map = new LinkedHashMap(initialCapacity);
     *        } else {
     *          this.map = new HashMap(initialCapacity);
     *        }
     *   }
     * </pre>
     *
     * @param jsonStr       JSON字符串
     * @param valueCls      转换为 Map的 Value值的 Class类型
     * @param <V>           Value 泛型
     * @return
     */
    public static <V> Map<String,V> parseMap(String jsonStr, Class<V> valueCls) {
        Map<String, V> result = new LinkedHashMap<String, V>();

        //添加顺序 Feature
        Map<String, Object> map = JSON.parseObject(jsonStr, DEFAULT_P_FEATURES);

        if (map != null && map.size() > 0)  {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object obj = entry.getValue();
                try {
                    V value = JSON.parseObject(JSON.toJSONString(obj), valueCls);
                    result.put(entry.getKey(), value);
                } catch (Exception e) {
                }
            }
        }

        return result;
    }

    public static <V> Map<String,V> parseMap(Object jsonObject, Class<V> valueCls) {
        String jsonStr = JsonUtil.toJsonString(jsonObject);
        return JsonUtil.parseMap(jsonStr, valueCls);
    }

    /**
     * 将Json字符串转为Class对应的JavaBean类
     *
     * 注：
     *   1. class对应的JavaBean比如有无参构造方法，且完备的getter setter
     *   2. class不可以是内部类，目前无法反射成功
     *   3. class不可以传入数组，List或Map。数组/List类型请使用#parseList() , Map类型使用 #parseMap()
     *
     * @param jsonString    Json字符串
     * @param beanClazz     JavaBean类
     * @param <T>
     * @return
     */
    public static <T> T parseBean(String jsonString, Class<T> beanClazz) {
        return parseBean(JsonUtil.parseJsonObject(jsonString), beanClazz);
    }

    /**
     * 将JsonObject转为Class对应的JavaBean类
     *
     * 注：
     *   1. class可以是某个JavaBean,或者基本的Jdk类型
     *   2. class不可以是内部类，目前无法反射成功
     *   3. List或者Map类型请使用#parseList() 或者 #parseMap()
     *
     * @param jsonObject    JsonObject
     * @param beanClazz     JavaBean类
     * @param <T>
     * @return
     */
    public static <T> T parseBean(JSONObject jsonObject, Class<T> beanClazz) {
        return TypeUtils.castToJavaBean(jsonObject, beanClazz);
    }

}
