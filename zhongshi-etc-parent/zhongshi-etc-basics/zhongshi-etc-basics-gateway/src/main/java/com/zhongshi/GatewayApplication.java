/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.zhongshi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.zhongshi.factory.BaseResultFactory;

import reactor.core.publisher.Mono;

/**
 * 
 * 功能说明：网关启动
 * 
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 创建日期：2019-08-03 ：1:32:00
 *
 * 版权归属：蓝河团队
 *
 * 协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableDiscoveryClient
public class GatewayApplication {

	public static String loadConfig;

	@Value("${spring.cloud.nacos.config.shared-dataids}")
	public void setLoadConfig(String loadConfig) {
		GatewayApplication.loadConfig = loadConfig;
	}

	public static void main(String[] args) {
		Long begin = System.currentTimeMillis();
		SpringApplicationBuilder builder = new SpringApplicationBuilder(GatewayApplication.class);
		try {
			builder.bannerMode(Mode.OFF).run(new String[] {});
		} catch (Exception e) {
			StackTraceElement stackTraceElement = e.getStackTrace()[0];
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("============  服务启动失败     Begin    ==============");
			System.err.println("                                      ");
			System.err.println("服务模块：" + BaseResultFactory.applicationName);
			System.err.println("                                      ");
			System.err.println("服务启动异常：" + e.getLocalizedMessage());
			System.err.println("                                      ");
			System.err.println("服务加载配置：" + loadConfig);
			System.err.println("                                      ");
			System.err.println("异常行数 ：" + stackTraceElement + "\r\n");
			System.err.println("                                      ");
			System.err.println("============  服务启动失败     End    ==============");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.err.println("                                      ");
			System.exit(0);
		}

		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("============  服务启动完成     Begin    ==============");
		System.out.println("                                      ");
		System.out.println("服务模块：" + BaseResultFactory.applicationName);
		System.out.println("                                      ");
		System.out.println("服务端口：127.0.0.1:" + BaseResultFactory.port);
		System.out.println("                                      ");
		System.out.println("服务加载配置：" + loadConfig);
		System.out.println("                                      ");
		System.out.println(
				"服务启动耗时：" + new SimpleDateFormat("mm:ss").format(new Date(System.currentTimeMillis() - begin)));
		System.out.println("                                      ");
		System.out.println("============  服务启动完成     End    ==============");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
	}

	// ----------------------------- 解决跨域 Begin -----------------------------

	private static final String ALL = "*";
	private static final String MAX_AGE = "3600L";

	@Bean
	public RouteDefinitionLocator discoveryClientRouteDefinitionLocator(DiscoveryClient discoveryClient,
			DiscoveryLocatorProperties properties) {
		return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
	}

	@Bean
	public ServerCodecConfigurer serverCodecConfigurer() {
		return new DefaultServerCodecConfigurer();
	}

	@Bean
	public WebFilter corsFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
			ServerHttpRequest request = ctx.getRequest();
			if (!CorsUtils.isCorsRequest(request)) {
				return chain.filter(ctx);
			}
			HttpHeaders requestHeaders = request.getHeaders();
			ServerHttpResponse response = ctx.getResponse();
			HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
			HttpHeaders headers = response.getHeaders();
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
			headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
			if (requestMethod != null) {
				headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
			}
			headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
			headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
			if (request.getMethod() == HttpMethod.OPTIONS) {
				response.setStatusCode(HttpStatus.OK);
				return Mono.empty();
			}
			return chain.filter(ctx);
		};
	}

	// ----------------------------- 解决跨域 End -----------------------------

	@Component
	@Primary
	@AllArgsConstructor
	class DocumentationConfig implements SwaggerResourcesProvider {
		///v2/api-docs?id=2&sign=0B30EF0E4BAD967092D5C6FAC5A82D3A
		public static final String API_URI = "v2/api-docs";
		private final RouteLocator routeLocator;
		private final GatewayProperties gatewayProperties;

		@Override
	    public List<SwaggerResource> get() {
	        List<SwaggerResource> resources = new ArrayList<>();
	        List<String> routes = new ArrayList<>();
	        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
	        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId())).forEach(route -> {
	            route.getPredicates().stream()
	                    .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
	                    .forEach(predicateDefinition -> resources.add(swaggerResource(route.getId(),
	                            predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
	                                    .replace("**", API_URI))));
	        });

	        return resources;
	    }

	    private SwaggerResource swaggerResource(String name, String location) {
	        SwaggerResource swaggerResource = new SwaggerResource();
	        swaggerResource.setName(name);
	        swaggerResource.setLocation(location);
	        swaggerResource.setSwaggerVersion("2.0");
	        return swaggerResource;
	    }
	
	}
}