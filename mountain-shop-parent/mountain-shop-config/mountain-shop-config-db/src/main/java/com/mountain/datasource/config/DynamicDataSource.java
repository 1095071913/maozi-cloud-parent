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

package com.mountain.datasource.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;


/**
 * 
 * 	功能说明：数据源配置
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-09-19 : 20:27:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@Component
@Primary
public class DynamicDataSource extends AbstractRoutingDataSource {
	@Autowired
	@Qualifier("select-datasource")
	private DataSource selectDataSource;

	@Autowired
	@Qualifier("update-datasource")
	private DataSource updateDataSource;

	/**
	 * 这个是主要的方法，返回的是生效的数据源名称
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDbType();
	}

	/**
	 * 配置数据源信息
	 */
	@Override
	public void afterPropertiesSet() {
		Map<Object, Object> map = new HashMap<>();
		map.put("select-datasource", selectDataSource);
		map.put("update-datasource", updateDataSource);
		setTargetDataSources(map);
		setDefaultTargetDataSource(updateDataSource);
		super.afterPropertiesSet();
	}
}