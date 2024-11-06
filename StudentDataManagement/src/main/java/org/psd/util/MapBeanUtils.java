package org.psd.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Map和Bean相互转换工具类
 *
 * @author pengshidun
 */
@Slf4j
public class MapBeanUtils {

    // 私有构造方法，防止实例化
    private MapBeanUtils() {
    }

    /**
     * 将Map转换为指定的Bean对象
     *
     * @param map       提供的参数Map
     * @param beanClass 要转换的Bean类对象
     * @param <T>       转换的Bean类型
     * @return 转换后的Bean对象，如果Map为空或转换失败则返回null
     */
    public static <T> T mapToBean(Map<String, ?> map, Class<T> beanClass) {
        if (map == null) {
            return null;
        }
        try {
            T obj = beanClass.newInstance();
            BeanUtils.populate(obj, map);
            return obj;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将Bean对象转换为Map
     *
     * @param obj 要转换的Bean对象
     * @return 转换后的Map，如果Bean对象为空则返回null
     */
    public static Map<?, ?> beanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(obj);
    }
}
