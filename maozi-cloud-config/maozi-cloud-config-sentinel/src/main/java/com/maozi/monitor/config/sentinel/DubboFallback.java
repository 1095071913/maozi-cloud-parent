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

package com.maozi.monitor.config.sentinel;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel Dubbo 降级配置
 * <p>
 * 注册 Sentinel 资源切面 {@link SentinelResourceAspect}，
 * 使 {@code @SentinelResource} 注解在 Dubbo 服务中生效，
 * 实现服务调用的流量控制和熔断降级。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class DubboFallback {

	/**
	 * 创建 Sentinel 资源切面 Bean
	 *
	 * @return SentinelResourceAspect 实例
	 */
	@Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

}
