package com.kevin.library.utils;
import com.kevin.library.config.RedisConfig;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import java.util.*;

public class ShiroRedisCache<K,V> implements Cache<K,V> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Jedis jedis = JedisUtils.getJedis();

    public Object get(Object key) throws CacheException {

        try {
            logger.info("从连接池中获取连接实例："+jedis);
            byte[] bs = ByteSourceUtils.serialize(key);
            byte[] value = jedis.get(bs);
            if (value == null) {
                return null;
            }
            logger.info("********获取缓存"+value);
            return ByteSourceUtils.deserialize(value);
        }catch (Exception ee){
            logger.info("错误："+ee);
        }finally {
            logger.info("放回连接池"+jedis);
            jedis.close();
        }
        logger.info("*************");
        return null;
    }

    public Object put(Object key, Object value) throws CacheException {

        try {
            logger.info("从连接池中获取连接实例："+jedis);
            jedis.set(ByteSourceUtils.serialize(key), ByteSourceUtils.serialize(value));
            byte[] bs = jedis.get(ByteSourceUtils.serialize(key));
            Object object = ByteSourceUtils.deserialize(bs);
            logger.info("*********放入缓存"+object);
            return object;
        }catch (Exception ee){
            logger.info("错误："+ee);
        }finally {
            logger.info("放回连接池"+jedis);
            jedis.close();
        }
        logger.info("*************");
        return null;
    }

    public Object remove(Object key) throws CacheException {

        try {
            logger.info("从连接池中获取连接实例："+jedis);
            byte[] bs = jedis.get(ByteSourceUtils.serialize(key));
            logger.info("*********"+bs);
            jedis.del(ByteSourceUtils.serialize(key));
            logger.info("清空缓存");
            return ByteSourceUtils.deserialize(bs);
        }catch (Exception ee){
            logger.info("错误:"+ee);
        }finally {
            logger.info("放回连接池"+jedis);
            jedis.close();
        }
        logger.info("*************");
        return null;
    }

    public void clear() throws CacheException {
        jedis.flushDB();
    }

    public int size() {
        Long size = jedis.dbSize();
        return size.intValue();
    }

    public Set keys() {

        try {
            logger.info("从连接池中获取连接实例："+jedis);
            Set<byte[]> keys = jedis.keys(new String("*").getBytes());
            Set<Object> set = new HashSet<Object>();
            for (byte[] bs : keys) {
                set.add(ByteSourceUtils.deserialize(bs));
            }
            return set;
        }catch (Exception ee){
            logger.info("错误:"+ee);
        }finally {
            logger.info("放回连接池"+jedis);
            jedis.close();
        }
        logger.info("*************");
        return null;
    }

    public Collection values() {

        try {
            logger.info("从连接池中获取连接实例："+jedis);
            Set keys = this.keys();
            List<Object> values = new ArrayList<Object>();
            for (Object object : keys) {
                byte[] bs = JedisUtils.getJedis().get(ByteSourceUtils.serialize(object));
                values.add(ByteSourceUtils.deserialize(bs));
            }
            return values;
        }catch (Exception ee){
            logger.info("错误："+ee);
        }finally {
            logger.info("放回连接池"+jedis);
            jedis.close();
        }
        logger.info("*************");
        return null;
    }


}
