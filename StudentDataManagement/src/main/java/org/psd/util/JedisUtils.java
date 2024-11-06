package org.psd.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Jedis连接工具类，提供Redis连接的获取和关闭功能。
 *
 * @author pengshidun
 */
@Slf4j
public class JedisUtils {

    /**
     * 存放Redis配置内容的Properties对象。
     */
    private static Properties properties;

    /**
     * 存放Jedis连接对象。
     */
    private static Jedis jedis;

    /**
     * 静态初始化块，加载Redis配置文件。
     */
    static {
        properties = new Properties();
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try (InputStream in = new FileInputStream(path + "redis-config.properties")) {
            properties.load(in);
        } catch (IOException e) {
            log.warn("加载Redis配置文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取配置的Jedis对象。
     *
     * @return Jedis操作对象
     */
    public static Jedis getJedis() {
        String host = properties.getProperty("redis.host");
        int port = Integer.parseInt(properties.getProperty("redis.port"));
        try {
            jedis = new Jedis(host, port);
            return jedis;
        } catch (JedisConnectionException | JedisDataException e) {
            log.warn("创建Jedis连接失败: " + e.getMessage(), e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 关闭Jedis连接。
     *
     * @param jedis 待关闭的Jedis对象
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
