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

    private final static String PROJECT_NACOS_SERVER_KEY = "nacos.server";

    private final static String PROJECT_NACOS_SERVER_VALUE = "${NACOS_SERVER:maozi-cloud-basic-nacos:8848}";

    private final static String PROJECT_NACOS_NAMESPACE_KEY = "nacos.namespace";

    private final static String PROJECT_NACOS_NAMESPACE_VALUE = "${NACOS_NAMESPACE:${environment}}";

    private final static String NACOS_CONFIG_SERVER_KEY = "spring.cloud.nacos.config.server-addr";

    private final static String NACOS_DISCOVERY_SERVER_KEY = "spring.cloud.nacos.discovery.server-addr";

    private final static String NACOS_CONFIG_NAMESPACE_KEY = "spring.cloud.nacos.config.namespace";

    private final static String NACOS_DISCOVERY_NAMESPACE_KEY = "spring.cloud.nacos.discovery.namespace";

    @Override
    public void initialize(Properties properties) {

        properties.put("spring.cloud.nacos.config.file-extension", "yml");
        properties.put("spring.cloud.nacos.config.refreshable-dataids","${spring.application.name}.yml");

        if(properties.containsKey(PROJECT_NACOS_SERVER_KEY)){
            properties.put(NACOS_CONFIG_SERVER_KEY, "${" + PROJECT_NACOS_SERVER_KEY + "}");
            properties.put(NACOS_DISCOVERY_SERVER_KEY, "${" + PROJECT_NACOS_SERVER_KEY + "}");
        }else{
            properties.put(NACOS_CONFIG_SERVER_KEY, PROJECT_NACOS_SERVER_VALUE);
            properties.put(NACOS_DISCOVERY_SERVER_KEY, PROJECT_NACOS_SERVER_VALUE);
        }

        if(properties.containsKey(PROJECT_NACOS_NAMESPACE_KEY)){
            properties.put(NACOS_CONFIG_NAMESPACE_KEY, "${" + PROJECT_NACOS_NAMESPACE_KEY + "}");
            properties.put(NACOS_DISCOVERY_NAMESPACE_KEY, "${" + PROJECT_NACOS_NAMESPACE_KEY + "}");
        }else{
            properties.put(NACOS_CONFIG_NAMESPACE_KEY, PROJECT_NACOS_NAMESPACE_VALUE);
            properties.put(NACOS_DISCOVERY_NAMESPACE_KEY, PROJECT_NACOS_NAMESPACE_VALUE);
        }

    }

    @Override
    public void appendLogs(Map<String, String> logs) {
        logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
    }

}
