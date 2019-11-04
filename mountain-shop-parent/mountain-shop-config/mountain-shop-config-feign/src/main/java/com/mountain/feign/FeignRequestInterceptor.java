
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

package com.mountain.feign;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Enumeration;

/**
 * 
 * 	功能说明：Feign携带Token
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {
	
	
	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		assert attributes != null;
		HttpServletRequest request = attributes.getRequest();
		// 设置请求头
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				String value = request.getHeader(name);
				requestTemplate.header(name, value);
			}
		}
		// 设置请求体，这里主要是为了传递 access_token
		Enumeration<String> parameterNames = request.getParameterNames();
		StringBuilder body = new StringBuilder();
		if (parameterNames != null) {
			while (parameterNames.hasMoreElements()) {
				String name = parameterNames.nextElement();
				String value = request.getParameter(name);
				// 将 Token 加入请求头
				if ("access_token".equals(name)) {
					requestTemplate.header("authorization", "Bearer " + value);
				}
				// 其它参数加入请求体
				else {
					body.append(name).append("=").append(value).append("&");
				}
			}
		}
		// 设置请求体
		if (body.length() > 0) {
			// 去掉最后一位 & 符号
			body.deleteCharAt(body.length() - 1);
			requestTemplate.body(Request.Body.bodyTemplate(body.toString(), Charset.defaultCharset()));
		}
	}
}