package com.kevin.library.utils;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.data.redis.core.RedisTemplate;

public class ShiroRedisCacheManager implements CacheManager {

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new ShiroRedisCache<K, V>();
    }
}