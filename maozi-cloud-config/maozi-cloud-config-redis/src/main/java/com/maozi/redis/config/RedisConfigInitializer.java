package com.maozi.redis.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 缓存运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@code BaseApplication} 加载，将 Redis 相关
 * Nacos 共享配置文件追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}。
 * </p>
 *
 * @author maozi
 */
public class RedisConfigInitializer implements ConfigInitializer {

    /** 缓存相关 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "boot-redis.yml";

    @Override
    public void initialize(Properties properties) {
        properties.merge(
            "spring.cloud.nacos.config.shared-dataids",
            SHARED_DATAIDS,
            (existing, incoming) -> existing + "," + incoming
        );
    }

    @Override
    public void appendLogs(Map<String, String> logs) {}

}
