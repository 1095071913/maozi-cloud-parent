package com.maozi.seata.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 分布式事务运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@code BaseApplication} 加载，将 Seata 相关
 * Nacos 共享配置文件追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}。
 * </p>
 *
 * @author maozi
 */
public class SeataConfigInitializer implements ConfigInitializer {

    /** 分布式事务相关 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-seata.yml";

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
