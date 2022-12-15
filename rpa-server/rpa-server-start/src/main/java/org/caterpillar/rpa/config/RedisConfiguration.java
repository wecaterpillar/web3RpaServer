package org.caterpillar.rpa.config;

import org.caterpillar.rpa.common.MyRedisCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//@EnableCaching(proxyTargetClass = true)
//@Configuration
public class RedisConfiguration
    extends org.jeecg.common.modules.redis.config.RedisConfig
//        extends CachingConfigurerSupport
{

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisConfiguration(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return cacheManager1();
    }

    public CacheManager cacheManager1() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 设置默认缓存有效期
                .entryTtl(Duration.ofMinutes(30));
        return new MyRedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                RedisCacheConfiguration.defaultCacheConfig());
    }

    public CacheManager cacheManager2() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // 设置缓存有效期
                .entryTtl(Duration.ofMinutes(30));
        // 配置缓存过期时间
        Map<String, RedisCacheConfiguration> cacheNameMap = new HashMap<>();
        // 设置test缓存空间有效期1个小时
        cacheNameMap.put("test", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
        // 设置test2缓存空间有效期2个小时
        cacheNameMap.put("test2", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)));

        Set<String> cacheNames = cacheNameMap.keySet();

        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                // 设置初始化缓存空间
                .initialCacheNames(cacheNames)
                // 加载配置
                .withInitialCacheConfigurations(cacheNameMap)
                .cacheDefaults(redisCacheConfiguration).build();
    }
}
