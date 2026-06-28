package com.maozi.config.spi;

import com.maozi.common.constant.LogTag;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * 分布式组件运行时配置初始化器
 * <p>
 * 通过 SPI 机制被 BaseApplication 加载，将 Nacos 配置中心
 * 连接信息和各组件共享配置文件写入系统属性。
 * </p>
 *
 * @author maozi
 */
public class NacosConfigInitializer implements ConfigInitializer {

    @Override
    public void initialize(Properties properties) {
        properties.put("spring.cloud.nacos.config.file-extension", "yml");
        properties.put("spring.cloud.nacos.config.server-addr", "${NACOS_CONFIG_SERVER:maozi-cloud-nacos:8848}");
    }

    @Override
    public void appendLogs(Map<String, String> logs) {
        logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
    }

}
