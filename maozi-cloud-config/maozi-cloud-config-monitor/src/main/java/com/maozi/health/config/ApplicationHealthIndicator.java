
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

import com.maozi.common.BaseCommon;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("Application-Run")
public class ApplicationHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {

		Set<Entry<String, Map<String, String>>> errorResult = BaseCommon.adminHealthError.entrySet();

		if (errorResult.size() != 0) {
			Builder down = Health.down();
			String keyPrefix = "";
			for (String key : BaseCommon.adminHealthError.keySet()) {

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

				Map<String, String> errorLogDetaileds = BaseCommon.adminHealthError.get(key);
				for (String errorLogDetailedKey : errorLogDetaileds.keySet()) {
					String errorDetailed = errorLogDetaileds.get(errorLogDetailedKey);
					if (!BaseCommon.isNull(errorDetailed)) {
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
