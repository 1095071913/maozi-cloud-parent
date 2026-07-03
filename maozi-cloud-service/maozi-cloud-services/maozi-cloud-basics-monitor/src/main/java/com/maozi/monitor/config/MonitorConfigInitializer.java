package com.maozi.monitor.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 监控服务运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，将监控服务所需的
 * Nacos 配置中心连接信息和共享配置文件写入系统属性。
 * </p>
 *
 * @author maozi
 */
public class MonitorConfigInitializer implements ConfigInitializer {

    /** 监控服务 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml";

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
