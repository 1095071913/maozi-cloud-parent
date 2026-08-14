package com.maozi.job.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 定时任务运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@code BaseApplication} 加载，将 XXL-Job 相关
 * Nacos 共享配置文件追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}。
 * </p>
 *
 * @author maozi
 */
public class JobConfigInitializer implements ConfigInitializer {

    /** 定时任务相关的 Nacos 共享配置文件 */
    private static final String SHARED_DATAIDS = "boot-job.yml";

    /**
     * 追加 XXL-Job 共享配置文件到 Nacos 共享配置列表。
     * <p>
     * 若 {@code spring.cloud.nacos.config.shared-dataids} 已有值，
     * 则以逗号分隔追加，保留其他模块已注册的共享配置文件。
     * </p>
     *
     * @param properties 系统属性容器
     */
    @Override
    public void initialize(Properties properties) {
        properties.merge(
            "spring.cloud.nacos.config.shared-dataids",
            SHARED_DATAIDS,
            (existing, incoming) -> existing + "," + incoming
        );
    }

    /** 空实现，XXL-Job 配置初始化无需向启动日志追加诊断信息。 */
    @Override
    public void appendLogs(Map<String, String> logs) {}

}
