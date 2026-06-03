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

package com.maozi.mvc.config.error;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.maozi.common.JacksonUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class ErrorParamTranslation {
	
	public static Map<String, String> errorParams;
	
	public ErrorParamTranslation() throws Exception {

		ConfigService configService = NacosFactory.createConfigService(ApplicationEnvironmentContext.CONFIG_ADDR);

		errorParamTranslation(configService.getConfig("saas-param-error.json", "DEFAULT_GROUP", 5000));

		configService.addListener("saas-param-error.json", "DEFAULT_GROUP", new Listener() {

			@Override
			public void receiveConfigInfo(String codeJson) {
				errorParamTranslation(codeJson);
			}

			@Override
			public Executor getExecutor() {
				return null;
			}

		});

	}
	
	public void errorParamTranslation(String json) {
		
		try {errorParams = JacksonUtil.jsonToMap(json);}catch (Exception e) {
			LogUtil.error(log,e);
		}

	}

}
