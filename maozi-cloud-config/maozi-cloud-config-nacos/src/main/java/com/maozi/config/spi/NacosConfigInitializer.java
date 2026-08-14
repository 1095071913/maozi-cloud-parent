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

    /** 项目自定义的 Nacos 服务地址属性键 */
    private final static String PROJECT_NACOS_SERVER_KEY = "nacos.server";

    /** Nacos 服务地址默认值占位符：优先取环境变量 NACOS_SERVER，缺省使用内置 Nacos 服务地址 */
    private final static String PROJECT_NACOS_SERVER_VALUE = "${NACOS_SERVER:maozi-cloud-basic-nacos:8848}";

    /** 项目自定义的 Nacos 命名空间属性键 */
    private final static String PROJECT_NACOS_NAMESPACE_KEY = "nacos.namespace";

    /** Nacos 命名空间默认值占位符：优先取环境变量 NACOS_NAMESPACE，缺省跟随当前运行环境 */
    private final static String PROJECT_NACOS_NAMESPACE_VALUE = "${NACOS_NAMESPACE:${environment}}";

    /** Nacos 配置中心服务地址属性键 */
    private final static String NACOS_CONFIG_SERVER_KEY = "spring.cloud.nacos.config.server-addr";

    /** Nacos 注册中心（服务发现）服务地址属性键 */
    private final static String NACOS_DISCOVERY_SERVER_KEY = "spring.cloud.nacos.discovery.server-addr";

    /** Nacos 配置中心命名空间属性键 */
    private final static String NACOS_CONFIG_NAMESPACE_KEY = "spring.cloud.nacos.config.namespace";

    /** Nacos 注册中心（服务发现）命名空间属性键 */
    private final static String NACOS_DISCOVERY_NAMESPACE_KEY = "spring.cloud.nacos.discovery.namespace";

    /**
     * 初始化 Nacos 配置中心与注册中心的连接属性。
     * <p>
     * 若项目属性中已存在 {@code nacos.server} / {@code nacos.namespace}，
     * 则以占位符方式引用对应的项目属性值，否则直接写入默认占位符；
     * 同时设置配置文件扩展名为 yml，并声明应用自身的配置文件支持动态刷新。
     * </p>
     *
     * @param properties 系统属性容器
     */
    @Override
    public void initialize(Properties properties) {

        // 设置 Nacos 配置文件的扩展名为 yml
        properties.put("spring.cloud.nacos.config.file-extension", "yml");

        // 声明应用自身的配置文件（{spring.application.name}.yml）支持动态刷新
        properties.put("spring.cloud.nacos.config.refreshable-dataids","${spring.application.name}.yml");

        // 若已存在项目级 nacos.server 定义，则配置中心与注册中心地址以占位符方式引用该值
        if(properties.containsKey(PROJECT_NACOS_SERVER_KEY)){
            properties.put(NACOS_CONFIG_SERVER_KEY, "${" + PROJECT_NACOS_SERVER_KEY + "}");
            properties.put(NACOS_DISCOVERY_SERVER_KEY, "${" + PROJECT_NACOS_SERVER_KEY + "}");
        }else{
            // 否则写入默认占位符：优先取环境变量 NACOS_SERVER，缺省使用内置 Nacos 服务地址
            properties.put(NACOS_CONFIG_SERVER_KEY, PROJECT_NACOS_SERVER_VALUE);
            properties.put(NACOS_DISCOVERY_SERVER_KEY, PROJECT_NACOS_SERVER_VALUE);
        }

        // 若已存在项目级 nacos.namespace 定义，则配置中心与注册中心命名空间以占位符方式引用该值
        if(properties.containsKey(PROJECT_NACOS_NAMESPACE_KEY)){
            properties.put(NACOS_CONFIG_NAMESPACE_KEY, "${" + PROJECT_NACOS_NAMESPACE_KEY + "}");
            properties.put(NACOS_DISCOVERY_NAMESPACE_KEY, "${" + PROJECT_NACOS_NAMESPACE_KEY + "}");
        }else{
            // 否则写入默认占位符：优先取环境变量 NACOS_NAMESPACE，缺省跟随当前运行环境
            properties.put(NACOS_CONFIG_NAMESPACE_KEY, PROJECT_NACOS_NAMESPACE_VALUE);
            properties.put(NACOS_DISCOVERY_NAMESPACE_KEY, PROJECT_NACOS_NAMESPACE_VALUE);
        }

    }

    /** 追加 Nacos 配置中心地址到启动日志。 */
    @Override
    public void appendLogs(Map<String, String> logs) {
        logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
    }

}
