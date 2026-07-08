package com.maozi.swagger.config;

import com.maozi.common.enums.EnvironmentType;
import com.maozi.common.spi.ConfigInitializer;

import java.util.Map;
import java.util.Properties;

/**
 * Swagger/OpenAPI 配置初始化器
 * <p>
 * 根据当前运行环境动态设置 {@code springdoc.api-docs.enabled} 属性：
 * 生产环境（prod）关闭 API 文档暴露，非生产环境开启，避免生产环境泄露接口元数据。
 * </p>
 *
 * @author maozi
 */
public class SwaggerConfigInitializer implements ConfigInitializer {

    @Override
    public void initialize(Properties properties) {
        String environment = (String) properties.get("environment");
        properties.put("springdoc.api-docs.enabled", !EnvironmentType.PROD.getDesc().equals(environment));
    }

    @Override
    public void appendLogs(Map<String, String> logs) {}

}
