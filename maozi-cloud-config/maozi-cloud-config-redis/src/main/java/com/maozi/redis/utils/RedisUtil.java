package com.maozi.redis.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.Getter;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author pengjinlong
 * @date 2026/5/8 18:17
 */
public class RedisUtil {

    public final static String REDIS_KEY_PREFIX;

    private final static String REDIS_KEY_PROJECT_PREFIX = "maozi-cloud";

    @Getter
    private final static StringRedisTemplate redisClient;

    static {

        redisClient = SpringUtil.getBean(StringRedisTemplate.class);

        REDIS_KEY_PREFIX = REDIS_KEY_PROJECT_PREFIX + ":" + ApplicationEnvironmentContext.SERVICE_NAME + ":";

    }

}
