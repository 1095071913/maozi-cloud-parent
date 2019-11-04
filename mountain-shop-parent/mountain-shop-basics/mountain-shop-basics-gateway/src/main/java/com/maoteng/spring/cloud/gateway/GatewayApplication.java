package com.maoteng.spring.cloud.gateway;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSwagger2WebFlux
public class GatewayApplication {
	// 注入配置文件上下文www
	@Autowired
	private ConfigurableApplicationContext applicationContext; 
	
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
     
    
    @Component
	@Primary
	class DocumentationConfig implements SwaggerResourcesProvider {
		@Override
		public List<SwaggerResource> get() {
			return resources();
		}
	}
        
    /**
	 * 获取swaggerDocument配置
	 * 
	 * @return
	 */
	private String swaggerDocument() { // 没有的话默认
		String property = applicationContext.getEnvironment().getProperty("mountain.gateway.swaggerDocument","");
		return property;
	}

	private SwaggerResource swaggerResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}

	/**
	 * 从NACOS服务器中获取resources
	 * 
	 * @return
	 */
	private List<SwaggerResource> resources() {

		List resources = new ArrayList<>();
		// app-itmayiedu-order
		// 网关使用服务别名获取远程服务的SwaggerApi
		String swaggerDocJson = swaggerDocument();
		JSONArray jsonArray = JSONArray.parseArray(swaggerDocJson);
		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject) object;
			String name = jsonObject.getString("name");
			String location = jsonObject.getString("location");
			String version = jsonObject.getString("version");
			resources.add(swaggerResource(name, location, version));
		}
		return resources;
	}
}