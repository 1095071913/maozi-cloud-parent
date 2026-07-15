package com.maozi.lock.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 分布式锁运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@code BaseApplication} 加载，将分布式锁相关
 * Nacos 共享配置文件追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}。
 * </p>
 *
 * @author maozi
 */
public class LockConfigInitializer implements ConfigInitializer {

    /** 分布式锁相关 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "boot-lock.yml";

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
