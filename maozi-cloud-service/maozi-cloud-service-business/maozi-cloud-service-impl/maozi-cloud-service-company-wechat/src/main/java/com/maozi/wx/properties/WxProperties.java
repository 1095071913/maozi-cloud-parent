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

package com.maozi.wx.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 企业微信配置属性
 * <p>
 * 从 {@code wx.company.config} 前缀下读取企业微信 API 的连接配置。
 * </p>
 *
 * @author maozi
 */
@Data
@Configuration
@ConfigurationProperties("wx.company.config")
public class WxProperties {

    /** 企业 ID */
	private String corpid;

    /** 通讯录密钥 */
	private String contactsCorpsecret;

    /** 部门 ID */
	private String departmentId;

    /** 回调 Token */
	private String sToken;

    /** 消息加解密密钥 */
	private String sEncodingAESKey;

}
