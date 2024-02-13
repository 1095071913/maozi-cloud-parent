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

package com.maozi;

import com.maozi.base.enums.EnvironmentType;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gitee.sunchenbin.mybatis.actable.manager.*")
@MapperScan(basePackages = {"com.maozi.${application-project-abbreviation}.*.mapper","com.gitee.sunchenbin.mybatis.actable.dao.*"})
public class BaseApplicationDB {

	protected static void ApplicationRun() {

		Properties properties = System.getProperties();

		String environment = System.getenv("environment");

		if(StringUtils.isEmpty(environment) || EnvironmentType.localhost.getDesc().equals(environment)){

			properties.put("actable.table.auto","update");

			properties.put("spring.flyway.enabled",false);

		}else{

			properties.put("actable.table.auto","none");

			properties.put("spring.flyway.enabled",true);

		}

		properties.put("application-nacos-config-service",",boot-datasource.yml,boot-db.yml");

		BaseApplication.ApplicationRun();

	}

}
