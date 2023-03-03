
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

package com.maozi.health.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.maozi.factory.BaseResultFactory;

/**
 *
 * 功能说明：BootAdmin心跳检查服务
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

@Component("Application-Run")
public class ApplicationHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {

		Set<Entry<String, Map<String, String>>> errorResult = BaseResultFactory.adminHealthError.entrySet();

		if (errorResult.size() != 0) {
			Builder down = Health.down();
			String keyPrefix = "";
			for (String key : BaseResultFactory.adminHealthError.keySet()) {

				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";

				Map<String, String> errorLogDetaileds = BaseResultFactory.adminHealthError.get(key);
				for (String errorLogDetailedKey : errorLogDetaileds.keySet()) {
					String errorDetailed = errorLogDetaileds.get(errorLogDetailedKey);
					if (!BaseResultFactory.isNull(errorDetailed)) {
						down.withDetail(errorLogDetailedKey, errorDetailed);
						keyPrefix += " ";
					}
				}
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
				down.withDetail(keyPrefix, "　⁣⁣⁣⁣　⁣⁣⁣⁣　　⁣⁣⁣⁣　⁣⁣⁣⁣　");
				keyPrefix += " ";
			}
			return down.down().build();
		}

		return Health.up().build();
	}

}
