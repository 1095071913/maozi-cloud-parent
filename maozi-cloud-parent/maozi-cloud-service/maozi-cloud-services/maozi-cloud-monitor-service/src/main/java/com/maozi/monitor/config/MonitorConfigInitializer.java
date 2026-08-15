package com.maozi.monitor.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 监控服务运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，将监控服务的
 * Nacos 共享配置文件清单追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}，
 * 属性已存在时以逗号拼接合并。
 * </p>
 *
 * @author maozi
 */
public class MonitorConfigInitializer implements ConfigInitializer {

    /** 监控服务 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml";

    /**
     * 追加监控服务的 Nacos 共享配置文件清单
     *
     * @param properties 系统属性
     */
    @Override
    public void initialize(Properties properties) {
        properties.merge(
            "spring.cloud.nacos.config.shared-dataids",
            SHARED_DATAIDS,
            (existing, incoming) -> existing + "," + incoming
        );
    }

    /**
     * 本初始化器无需输出额外的启动诊断信息，空实现
     *
     * @param logs 启动日志容器
     */
    @Override
    public void appendLogs(Map<String, String> logs) {}

}
