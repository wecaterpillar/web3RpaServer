package org.caterpillar.rpa.common;

import cn.hutool.core.util.NumberUtil;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**java
 * 自定义cacheNames方式 RedisCacheManager
 * 自定义cacheNames方式可以设置过期时间 格式 name#12s  标识12秒过期(d=天,h=小时,m=分钟,s秒)
 */

public class MyRedisCacheManager extends RedisCacheManager {

    public MyRedisCacheManager(RedisCacheWriter cacheWriter,
                               RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        String[] array = StringUtils.delimitedListToStringArray(name, "#");
        name = array[0];
        if (array.length > 1) { // 解析TTL
            // 例如 12 默认12秒， 12d=12天
            String ttlStr = array[1];
            Duration duration = convertDuration(ttlStr);
            cacheConfig = cacheConfig.entryTtl(duration);
        }
        return super.createRedisCache(name, cacheConfig);
    }

    private Duration convertDuration(String ttlStr) {
        if (NumberUtil.isLong(ttlStr)) {
            return Duration.ofSeconds(Long.parseLong(ttlStr));
        }

        ttlStr = ttlStr.toUpperCase();

        if (ttlStr.lastIndexOf("D") != -1) {
            return Duration.parse("P" + ttlStr);
        }

        return Duration.parse("PT" + ttlStr);
    }

}
