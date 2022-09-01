
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

package com.jiumao.generate.code.parent.service;

import com.jiumao.generate.code.tool.SQLType;

/**
 * 
 * 	功能说明：生成bootstrap.properties
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-24 : 0:24:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class GenerateProperties {

	public static void generate(String mobule,String pash) throws Exception {

			StringBuilder properties = new StringBuilder();
			
			properties.append("spring.application.name=jiumao-saas-"+mobule+"\r\n");
			properties.append("spring.cloud.nacos.config.file-extension=yml\r\n");
			properties.append("spring.cloud.nacos.config.server-addr=${NACOS_CONFIG_SERVER:localhost:8848}\r\n");
			properties.append("spring.cloud.nacos.config.shared-dataids=cloud-default.yml,cloud-nacos.yml,cloud-dubbo.yml,cloud-sentinel.yml,cloud-security.yml,boot-redis.yml,boot-admin.yml,boot-datasource.yml,boot-mybatisplus.yml,boot-swagger.yml\r\n");
			properties.append("#spring.cloud.nacos.config.refreshable-dataids    ${nacos.config.server-addr}\r\n\r\n");
			properties.append("logging.level.root=ERROR\r\n");
			properties.append("logging.level.com.jiumao=INFO\r\n");
			properties.append("logging.file.name=log/log.log\r\n");
			properties.append("logging.config=classpath:log.xml");

			/* 创建文件 Begin */
			SQLType.fileCreate(pash, "bootstrap", properties,"properties");
			/* 创建文件 End */


	}
	
}
