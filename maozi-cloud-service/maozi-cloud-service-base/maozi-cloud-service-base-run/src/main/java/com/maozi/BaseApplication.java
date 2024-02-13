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

package com.maozi;

import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@DependsOn({"applicationEnvironmentContext","springUtil"})
public class BaseApplication {

	protected static void ApplicationRun() {
		
		Properties properties = System.getProperties();

		properties.put("spring.main.allow-circular-references",true);
		properties.put("spring.application.name", "maozi-cloud-${application-project-abbreviation}");
		properties.put("spring.cloud.nacos.config.file-extension", "yml");
		properties.put("spring.cloud.nacos.config.server-addr", "${NACOS_CONFIG_SERVER:maozi-cloud-nacos:8848}");
		properties.put("application-nacos-config-basics","cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml");

		Object serviceConfig = properties.get("application-nacos-config-service");

		properties.put("application-nacos-config-service","cloud-nacos.yml,cloud-dubbo.yml,cloud-sentinel.yml,boot-monitor.yml,api-whitelist.yml,cloud-oauth.yml,boot-redis.yml,boot-swagger.yml,boot-lock.yml,boot-arthas.yml,cloud-default.yml"+(Objects.nonNull(serviceConfig) ? serviceConfig.toString() : ""));

		properties.put("logging.level.root", "ERROR");
		properties.put("logging.level.com.maozi", "INFO");
		properties.put("logging.file.name","log/log.log");

		Long begin = System.currentTimeMillis();

		SpringApplicationBuilder builder = new SpringApplicationBuilder(BaseApplication.class);

		Map<String, String> logs = new LinkedHashMap<String, String>();

		Boolean errorBoo = false;

		try {

			builder.bannerMode(Mode.OFF).run(new String[] {});
			logs.put("InitTime", (System.currentTimeMillis() - begin) + " ms");
			logs.put("Nacos", ApplicationEnvironmentContext.nacosAddr);
			logs.put("Config", ApplicationEnvironmentContext.loadConfig);

		}
		catch (Exception e) {

			log.error(BaseCommon.getStackTrace(e));

			errorBoo = true;
			StackTraceElement stackTraceElement = e.getStackTrace()[0];
			logs.put("Nacos", ApplicationEnvironmentContext.nacosAddr);
			logs.put("Config", ApplicationEnvironmentContext.loadConfig);
			logs.put("ErrorDesc", e.getLocalizedMessage());
			logs.put("ErrorLine", stackTraceElement.toString());

		}
		finally {

			if (errorBoo) {
				log.error(BaseCommon.appendLog(logs).toString());
				System.exit(0);
			}
			else {
				log.info(BaseCommon.appendLog(logs).toString());
			}
			
			BaseCommon.clearContext();

		}

	}

}
