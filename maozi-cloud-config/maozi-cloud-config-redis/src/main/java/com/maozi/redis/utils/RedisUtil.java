package com.maozi.redis.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.Getter;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 工具类
 * <p>
 * 提供 Redis 客户端实例和全局键前缀常量。
 * 键前缀格式为 {@code maozi-cloud:<服务名>:}，确保不同服务的键互不冲突。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/5/8 18:17
 */
public class RedisUtil {

    /** Redis 全局键前缀，格式：maozi-cloud:<服务名>: */
    public final static String REDIS_KEY_PREFIX;

    /** Redis 项目前缀 */
    private final static String REDIS_KEY_PROJECT_PREFIX = "maozi-cloud";

    /** StringRedisTemplate 客户端实例 */
    @Getter
    private final static StringRedisTemplate redisClient;

    static {

        redisClient = SpringUtil.getBean(StringRedisTemplate.class);

        REDIS_KEY_PREFIX = REDIS_KEY_PROJECT_PREFIX + ":" + ApplicationEnvironmentContext.SERVICE_NAME + ":";

    }

}
