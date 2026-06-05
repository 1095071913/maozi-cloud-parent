/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.maozi.swagger.config;

import cn.hutool.core.collection.CollUtil;
import com.maozi.common.CollectionUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.oauth.properties.ApiWhitelistProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import jakarta.annotation.Resource;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class SwaggerConfig {

    private final static String LICENSE_NAME = "Apache 2.0";

    private final static String AUTHORIZATION = "Authorization";

    private final static String AUTHORIZATION_VALUE = "{{oauth_access_token}}";

    private final static String OFFICIAL_URL = "https://github.com/1095071913";

    @Resource
    private ApiWhitelistProperties apiWhitelist;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(
            new Info()
                .title(ApplicationEnvironmentContext.TITLE)
                .version(ApplicationEnvironmentContext.VERSION)
                .description(ApplicationEnvironmentContext.DETAILS)
                .termsOfService(OFFICIAL_URL)
                .license(new License().name(LICENSE_NAME).url(OFFICIAL_URL))
        );
    }

    @Bean
    public OperationCustomizer globalHeaderOperation() {

        List<String> whitelist = CollectionUtil.newArrayList();
        whitelist.addAll(ApiWhitelistProperties.DEFAULT_WITE_LIST);
        whitelist.addAll(apiWhitelist.getConfigWhitelist());

        return (Operation operation, HandlerMethod handlerMethod) -> {

            String url = Objects.requireNonNull(handlerMethod.getMethodAnnotation(RequestMapping.class)).value()[0];
            if (whitelist.contains(url)) {
                return operation;
            }

            Parameter tokenHeader = new HeaderParameter()
                    .name(AUTHORIZATION)
                    .example(AUTHORIZATION_VALUE)
                    .description("用户授权令牌")
                    .required(true)
                    .schema(new io.swagger.v3.oas.models.media.StringSchema());
            operation.addParametersItem(tokenHeader);

            return operation;

        };

    }

    @Bean
    public OpenApiCustomizer longToStringResult() {

        return openApi -> {

            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }

            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            if (CollUtil.isEmpty(schemas)) {
                return;
            }

            for (Schema schema : schemas.values()) {
                if (schema.getProperties() == null) {
                    continue;
                }

                Map<String, Schema> properties = schema.getProperties();
                properties.forEach((fieldName, fieldSchema) -> {
                    if ("integer".equals(fieldSchema.getType()) && "int64".equals(fieldSchema.getFormat())) {
                        fieldSchema.setType("string");
                        fieldSchema.setFormat(null);
                    }
                });
            }

        };
    }

}