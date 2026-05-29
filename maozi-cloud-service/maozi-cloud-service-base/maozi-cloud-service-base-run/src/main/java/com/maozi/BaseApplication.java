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

import cn.hutool.extra.spring.SpringUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.constant.LogTag;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@Import({SpringUtil.class})
@DependsOn({"applicationEnvironmentContext"})
public class BaseApplication {

	protected static void ApplicationRun(String[] args) {

		initProperties();

		long begin = System.currentTimeMillis();

		SpringApplicationBuilder builder = new SpringApplicationBuilder(BaseApplication.class);

		Map<String, String> logs = new LinkedHashMap<>();

		try {

			builder.bannerMode(Mode.OFF).run(args);
			logs.put(LogTag.INIT_TIME, (System.currentTimeMillis() - begin) + " ms");

			logs.put(LogTag.SERVICE_PORT, ApplicationEnvironmentContext.SERVICE_PORT);
			logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
			logs.put(LogTag.CONFIG, ApplicationEnvironmentContext.LOAD_CONFIG);

			LogUtil.info(log,logs);

		} catch (Exception e) {

			LogUtil.error(log,e);

			StackTraceElement stackTraceElement = e.getStackTrace()[0];

			logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
			logs.put(LogTag.CONFIG, ApplicationEnvironmentContext.LOAD_CONFIG);
			logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());
			logs.put(LogTag.ERROR_LINE, stackTraceElement.toString());

			LogUtil.error(log,logs);
			System.exit(0);

		}

		ApplicationEnvironmentContext.IS_RUNNING = true;

	}

	private static void initProperties() {

		Properties properties = System.getProperties();

		properties.put("spring.main.log-startup-info",false);
		properties.put("spring.main.allow-circular-references",true);
		properties.put("spring.application.name", "maozi-cloud-${application-project-abbreviation}");
		properties.put("spring.cloud.nacos.config.file-extension", "yml");
		properties.put("spring.cloud.nacos.config.server-addr", "${NACOS_CONFIG_SERVER:maozi-cloud-nacos:8848}");
		properties.put("application-nacos-config-basics","cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml");

		properties.put("logging.level.root", "ERROR");
		properties.put("logging.level.com.maozi", "INFO");
		properties.put("logging.file.name","log/log.log");

		properties.compute("application-nacos-config-service", (k, serviceConfig) -> "cloud-nacos.yml,cloud-dubbo.yml,cloud-sentinel.yml,boot-monitor.yml,api-whitelist.yml,cloud-oauth.yml,boot-redis.yml,boot-swagger.yml,boot-lock.yml,boot-arthas.yml,cloud-default.yml" + (ObjectUtil.isNotNullEmpty(serviceConfig) ? "," + serviceConfig : ""));

	}

}
