package com.maozi.base.plugin.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 数据库运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，将数据库相关
 * Nacos 共享配置文件追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}。
 * </p>
 *
 * @author maozi
 */
public class DbConfigInitializer implements ConfigInitializer {

    /** 数据库相关 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "boot-datasource.yml,boot-db.yml";

    /**
     * 初始化数据库运行时配置
     * <p>
     * 将数据库相关 Nacos 共享配置文件追加到系统属性
     * {@code spring.cloud.nacos.config.shared-dataids}。
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

    /** 本初始化器无需输出额外的启动诊断日志 */
    @Override
    public void appendLogs(Map<String, String> logs) {}

}
