package com.maozi.gateway.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 网关运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，将网关所需的
 * Nacos 配置中心连接信息和共享配置文件写入系统属性。
 * </p>
 *
 * @author maozi
 */
public class GatewayConfigInitializer implements ConfigInitializer {

    /** 网关 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml,cloud-sentinel.yml,boot-swagger.yml";

    @Override
    public void initialize(Properties properties) {
        properties.merge(
            "spring.cloud.nacos.config.shared-dataids",
            SHARED_DATAIDS,
            (existing, incoming) -> existing + "," + incoming
        );
    }

    @Override
    public void appendLogs(Map<String, String> logs) {
    }

}
