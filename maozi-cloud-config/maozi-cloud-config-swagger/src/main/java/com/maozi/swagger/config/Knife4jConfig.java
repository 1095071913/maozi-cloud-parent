/*
 * Copyright (C) 2018 Zhejiang xiaominfo Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.xiaominfo.com.
 * Developer Web Site: http://open.xiaominfo.com.
 */

package com.maozi.swagger.config;

import com.google.common.collect.Lists;
import com.maozi.common.BaseCommon;
import com.maozi.oauth.properties.ApiWhitelistProperties;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
public class Knife4jConfig {

    private static final String headerName = "Authorization";

    @Resource
    private ApiWhitelistProperties apiWhitelist;

    @Bean
    public OpenAPI customOpenAPI() {

        Components components = new Components();

        SecurityScheme securityScheme = new SecurityScheme();

        securityScheme.addExtension(headerName, "{{token}}");

        components.addSecuritySchemes(headerName,securityScheme

            .type(SecurityScheme.Type.APIKEY)
            .scheme("Bearer")
            .name(headerName)
            .in(SecurityScheme.In.HEADER)
            .description("请求头")

        );

        return new OpenAPI()
            .components(components)
            .info(new Info()
                .title(ApplicationEnvironmentContext.title)
                .version(ApplicationEnvironmentContext.version)
                .description(ApplicationEnvironmentContext.details)
                .termsOfService("http://maozi.com")
                .license(new License().name("Apache 2.0").url("http://maozi.com")));
    }

    @Bean
    public GroupedOpenApi group() {

        List<String> apiWhitelistAll = Lists.newArrayList();

        apiWhitelistAll.addAll(apiWhitelist.getWhitelist());

        apiWhitelistAll.addAll(apiWhitelist.getDefaultWitelist());

        return GroupedOpenApi.builder().group(ApplicationEnvironmentContext.applicationName)

            .addOperationCustomizer((operation, handlerMethod) -> {

                RequestMapping annotation = handlerMethod.getMethodAnnotation(RequestMapping.class);

                if(BaseCommon.isNotNull(annotation)){

                    String [] paths = annotation.value();

                    AntPathMatcher antPathMatcher = new AntPathMatcher();

                    Arrays.stream(paths).forEach(path ->{

                        String convertPath = path.replaceFirst("\\{\\w+\\}", "*");

                        if(
                            apiWhitelistAll.stream().filter(whitePath ->{
                                return antPathMatcher.match(whitePath,convertPath);
                            }).collect(Collectors.toList()).size() < 1
                        ){
                            operation.addSecurityItem(new SecurityRequirement().addList(headerName,"{{token}}"));
                        };

                    });

                }

                return operation;

            }).build();

    }

}