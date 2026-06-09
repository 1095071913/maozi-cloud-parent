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

package com.maozi.bd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 百度地图配置属性
 * <p>
 * 从 {@code bd.config} 前缀下读取百度地图 API 的连接配置。
 * </p>
 *
 * @author maozi
 */
@Data
@Configuration
@ConfigurationProperties("bd.config")
public class BDProperties {

    /** 百度地图 API 地址 */
	private String url;

    /** 百度地图 API 密钥 */
	private String ak;

}
