/*
 * Copyright 2012-2019 the original author or authors.
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
 */

package com.mountain;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import com.mountain.factory.BaseResultFactory;
import com.spring4all.swagger.EnableSwagger2Doc;


/**
 * 
 * 	功能说明：领域模型服务（无DB）启动
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@EnableElasticsearchRepositories(basePackages = "com.mountain.repository")
@EnableDiscoveryClient
@EnableFeignClients  
@EnableSwagger2Doc
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,  
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class BaseApplication extends BaseResultFactory {
	
	public static String loadConfig;
	
	@Value("${spring.cloud.nacos.config.shared-dataids}")
	public void setLoadConfig(String loadConfig) {
		
		BaseApplication.loadConfig=loadConfig;
	}
	
	protected static void ApplicationRun(Class applicationClass) {
		Long begin=System.currentTimeMillis();
		SpringApplicationBuilder builder = new SpringApplicationBuilder(applicationClass);
		try{
			builder.bannerMode(Mode.OFF).run(new String[] {});
		}catch (Exception e) {
			StackTraceElement stackTraceElement= e.getStackTrace()[0];
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
			System.err.println("服务模块："+applicationName);
			System.err.println("                                      ");
			System.err.println("服务启动异常："+e.getLocalizedMessage());
			System.err.println("                                      ");
			System.err.println("服务加载配置："+loadConfig);
			System.err.println("                                      ");
			System.err.println("异常行数 ："+stackTraceElement+"\r\n");
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
		System.out.println("服务模块："+applicationName);
		System.out.println("                                      ");
		System.out.println("服务端口：127.0.0.1:"+port);
		System.out.println("                                      ");
		System.out.println("服务加载配置："+loadConfig);
		System.out.println("                                      ");
		System.out.println("服务启动耗时："+new SimpleDateFormat("mm:ss").format(new Date(System.currentTimeMillis()-begin)));
		System.out.println("                                      ");
		System.out.println("============  服务启动完成     End    ==============");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		System.out.println("                                      ");
		
		
		
		
	}
	
	
	
}
