package com.maozi.service.spi;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 分布式组件运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，将分布式组件所需的
 * Nacos 共享配置文件清单追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}；
 * Nacos 配置中心连接信息由 maozi-cloud-config-nacos 模块的
 * {@code com.maozi.config.spi.NacosConfigInitializer} 负责写入。
 * </p>
 *
 * @author maozi
 */
public class NacosConfigInitializer implements ConfigInitializer {

    /** 分布式组件 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-nacos.yml,cloud-dubbo.yml,cloud-sentinel.yml,boot-monitor.yml,cloud-oauth.yml,boot-arthas.yml,cloud-default.yml";

    /**
     * 初始化分布式组件运行时配置
     * <p>
     * 将分布式组件所需的 Nacos 共享配置文件追加到系统属性
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
