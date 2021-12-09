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

package com.zhongshi;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.tool.ApplicationEnvironmentConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能说明：领域模型服务（无DB）启动
 * <p>
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * <p>
 * 创建日期：2019-08-03 ：1:32:00
 * <p>
 * 版权归属：蓝河团队
 * <p>
 * 协议说明：Apache2.0（ 文件顶端 ）
 */

@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@DependsOn({"applicationEnvironmentConfig"})
public class BaseApplication {

	protected static void ApplicationRun() {

		Long begin = System.currentTimeMillis();

		SpringApplicationBuilder builder = new SpringApplicationBuilder(BaseApplication.class);

		Map<String, String> logs = new LinkedHashMap<String, String>();

		Boolean errorBoo = false;

		try {

			builder.bannerMode(Mode.OFF).run(new String[] {});
			logs.put("uptime", (System.currentTimeMillis() - begin) + " ms");
			logs.put("config", ApplicationEnvironmentConfig.loadConfig);
			logs.put("nacosAddr", ApplicationEnvironmentConfig.nacosAddr + " net");
			logs.put("subscribe", ApplicationEnvironmentConfig.rpcServerNames);

		}
		catch (Exception e) {

			log.error(BaseResultFactory.getStackTrace(e));

			errorBoo = true;
			StackTraceElement stackTraceElement = e.getStackTrace()[0];
			logs.put("config", ApplicationEnvironmentConfig.loadConfig);
			logs.put("nacosAddr", ApplicationEnvironmentConfig.nacosAddr + " net");
			logs.put("errorDesc", e.getLocalizedMessage());
			logs.put("errorLine", stackTraceElement.toString());

		}
		finally {

			if (errorBoo) {
				log.error(BaseResultFactory.appendLog(logs).toString());
				System.exit(0);
			}
			else {
				log.info(BaseResultFactory.appendLog(logs).toString());
			}

		}

	}

}
