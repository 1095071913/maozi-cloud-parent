package com.maozi.service.spi;

import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 分布式组件运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 {@link com.maozi.BaseApplication} 加载，将 Nacos 配置中心
 * 连接信息和各组件共享配置文件写入系统属性。
 * </p>
 *
 * @author maozi
 */
public class NacosConfigInitializer implements ConfigInitializer {

    /** 分布式组件 Nacos 共享配置文件清单 */
    private static final String SHARED_DATAIDS = "cloud-nacos.yml,cloud-dubbo.yml,cloud-sentinel.yml,boot-monitor.yml,api-whitelist.yml,cloud-oauth.yml,boot-redis.yml,boot-lock.yml,boot-arthas.yml,cloud-default.yml";

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
