package com.kevin.library.utils;
import com.kevin.library.config.RedisConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis工具类
 */
public class JedisUtils {

    private static JedisPool jedisPool;

    /**
     * 初始化jedis连接池
     */
    static {
        JedisPoolConfig jedisConfig = new JedisPoolConfig();

        jedisConfig.setMaxTotal(20);
        jedisConfig.setMaxIdle(5);
        jedisConfig.setMinIdle(3);
        jedisConfig.setLifo(true);

        jedisConfig.setMaxWaitMillis(1000*100);

        jedisConfig.setBlockWhenExhausted(false);
        jedisConfig.setJmxEnabled(true);

        //检查连接是否可用
        jedisConfig.setTestWhileIdle(true);
        jedisConfig.setTimeBetweenEvictionRunsMillis(1000);
        jedisConfig.setTestOnBorrow(true);
        jedisConfig.setTestOnReturn(true);

        jedisConfig.setNumTestsPerEvictionRun(5);

        jedisPool = new JedisPool(jedisConfig, "127.0.0.1", 6379,1000,"123456");
    }

    /**
     * 从jedis连接池中获取一个连接
     * @return
     */
    public static Jedis getJedis() {
       return jedisPool.getResource();
    }

    /**
     * 关闭连接，放回连接池
     * @param jedis
     */
    public static void close(Jedis jedis) {
        jedis.close();
    }

}
