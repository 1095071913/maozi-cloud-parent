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
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 错误参数翻译配置
 * <p>
 * 从 Nacos 配置中心加载参数校验错误信息的翻译映射表（saas-param-error.json），
 * 并监听配置变更实时更新。用于将参数校验错误码转换为用户友好的中文提示信息。
 * </p>
 *
 * @author maozi
 */
@Slf4j
@Configuration
public class ErrorParamTranslation {

	/** 错误参数翻译映射表 */
	public static Map<String, String> errorParams;

	/**
	 * 构造方法，从 Nacos 加载配置并注册变更监听器
	 *
	 * @throws Exception Nacos 连接或配置加载异常
	 */
	public ErrorParamTranslation() throws Exception {

		// 创建 Nacos 配置服务客户端，使用应用环境中的配置中心地址
		String configAddr = ApplicationEnvironmentContext.CONFIG_ADDR;
		if(ObjectUtil.isNullEmpty(configAddr)){
			return;
		}
		ConfigService configService = NacosFactory.createConfigService(configAddr);

		// 首次加载：从 Nacos 获取 saas-param-error.json 配置内容，超时时间 5 秒
		errorParamTranslation(configService.getConfig("saas-param-error.json", "DEFAULT_GROUP", 5000));

		// 注册配置变更监听器，当 Nacos 上的配置发生变化时自动更新本地映射表
		configService.addListener("saas-param-error.json", "DEFAULT_GROUP", new Listener() {

			@Override
			public void receiveConfigInfo(String codeJson) {
				// 收到配置变更通知后重新解析并更新映射表
				errorParamTranslation(codeJson);
			}

			@Override
			public Executor getExecutor() {
				// 返回 null 表示使用 Nacos 默认的回调线程池执行监听回调
				return null;
			}

		});

	}

	/**
	 * 解析 JSON 配置并更新错误参数映射表
	 *
	 * @param json 错误参数翻译配置的 JSON 字符串
	 */
	public void errorParamTranslation(String json) {

		try {
			// 将 JSON 字符串解析为 Map<String, String> 映射表
			errorParams = JacksonUtil.jsonToMap(json);
		}catch (Exception e) {
			// 配置解析失败时记录错误日志，不影响应用启动
			LogUtil.error(log,e);
		}

	}

}
