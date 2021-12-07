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

import javax.sql.DataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * 功能说明：领域模型服务（DB）启动
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


@SpringBootApplication
@ComponentScan(basePackages = "com.gitee.sunchenbin.mybatis.actable.manager.*")
@MapperScan(basePackages = {"com.zhongshi.*.mapper","com.gitee.sunchenbin.mybatis.actable.dao.*"})
public class BaseApplicationDB {

	@Bean(name = "select-datasource")
	@ConfigurationProperties(prefix = "spring.datasource.select-datasource")
	public DataSource selectDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "update-datasource")
	@ConfigurationProperties(prefix = "spring.datasource.update-datasource")
	public DataSource updateDataSource() {
		return DataSourceBuilder.create().build();
	}
	

	protected static void ApplicationRun() {
		BaseApplication.ApplicationRun();
	}

}
