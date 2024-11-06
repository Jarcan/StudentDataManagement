package org.psd.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * Jackson 工具类，提供对象与JSON字符串、Map之间的转换功能。
 *
 * @author pengshidun
 */
@Slf4j
public class JacksonUtils {
    /**
     * 该工具类中用于操作bean、json、map等相互转换的对象。
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 获取创建的ObjectMapper实例。
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }

    /**
     * 将对象转成JSON字符串。
     *
     * @param obj 待转换的对象
     * @return JSON字符串
     */
    public static String objectToJsonStr(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("{} json序列化出错：" + obj, e.getMessage(), e);
            return "";
        }
    }

    /**
     * 将Map转成指定的Bean。
     *
     * @param map   待转换的Map对象
     * @param clazz Bean的类型
     * @return 转换成功后的Bean
     */
    public static <T> T mapToBean(Map map, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(objectToJsonStr(map), clazz);
        } catch (IOException e) {
            log.warn("{} json序列化出错：" + map, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将Bean转成Map。
     *
     * @param obj    待转换的对象
     * @param kClass Map中key的类型
     * @param vClass Map中value的类型
     * @return 转换成功后的Map
     */
    public static <K, V> Map<K, V> beanToMap(Object obj, Class<K> kClass, Class<V> vClass) {
        try {
            // 获取Map集合泛型的类型
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(Map.class, kClass, vClass);
            return OBJECT_MAPPER.readValue(objectToJsonStr(obj), javaType);
        } catch (IOException e) {
            log.warn("{} json序列化出错：" + obj, e.getMessage(), e);
            return null;
        }
    }
}
