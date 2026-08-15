package com.maozi.gateway.config;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 网关运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，开启同名单
 * Bean 定义覆盖，并将网关所需的 Nacos 共享配置文件清单写入系统属性。
 * </p>
 *
 * @author maozi
 */
public class GatewayConfigInitializer implements ConfigInitializer {

    /** 网关 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml,cloud-sentinel.yml";

    /**
     * 初始化网关运行时配置
     * <p>
     * 开启同名单 Bean 定义覆盖，并将网关所需的 Nacos 共享配置文件
     * 追加到系统属性 {@code spring.cloud.nacos.config.shared-dataids}。
     * </p>
     *
     * @param properties 系统属性容器
     */
    @Override
    public void initialize(Properties properties) {

        properties.put("spring.main.allow-bean-definition-overriding",true);

        properties.merge(
            "spring.cloud.nacos.config.shared-dataids",
            SHARED_DATAIDS,
            (existing, incoming) -> existing + "," + incoming
        );

    }

    /** 本初始化器无需输出额外的启动诊断日志 */
    @Override
    public void appendLogs(Map<String, String> logs) {
    }

}
