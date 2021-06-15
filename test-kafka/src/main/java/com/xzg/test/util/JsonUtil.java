package com.xzg.test.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author XieZG
 * @date 2018/5/22
 */
public class JsonUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    public static ObjectMapper mapper = new ObjectMapper();

    public static String writeJson(Object obj) {
        String json = "";
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("对象转换成json失败：{}", e.getMessage(), e);
        }
        return json;
    }

    public static <T> T mapToVo(Map<String, Object> map, Class<T> clz) {
        return mapper.convertValue(map, clz);
    }

    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = null;
        try {
            map = mapper.readValue(json, Map.class);
        } catch (IOException e) {
            LOGGER.error("json转换成对象失败：{}", e.getMessage(), e);
        }
        return map;
    }


    public static <T> T jsonToObj(String json, Class<T> clz) {
        try {
            return mapper.readValue(json, clz);
        } catch (IOException e) {
            LOGGER.error("json转换成对象失败：{}", e.getMessage(), e);
        }
        return null;
    }


    public static List<Object> jsonToArray(String json) {
        List<Object> list = null;
        try {
            list = mapper.readValue(json, List.class);
        } catch (IOException e) {
            LOGGER.error("json转换成对象失败：{}", e.getMessage(), e);
        }
        return list;
    }


    public static List<String> jsonToStrArray(String json) {
        List<String> list = null;
        try {
            list = mapper.readValue(json, List.class);
        } catch (IOException e) {
            LOGGER.error("json转换成对象失败：{}", e.getMessage(), e);
        }
        return list;
    }

    public static String generate32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
