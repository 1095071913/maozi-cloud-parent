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

package com.zhongshi.factory.result.error;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.code.CodeAttribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 
 * 	功能说明：结果集错误信息
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-01 ：5:22:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResult extends AbstractBaseResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7983254264886280345L;

	private Long id;
	
	private String applicationName;
	
	private String url;
	
	private String date;

	private CodeAttribute codeData;
	

	public ErrorResult(Long id,CodeAttribute codeData,String applicationName) {
		this(id,applicationName,isRESTorRPC(),new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date()),codeData);
		this.setCode(codeData.getCode());
	}
	
	
	private static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		return BaseResultFactory.isNotNull(servletRequestAttributes) ? null : servletRequestAttributes.getRequest();
	}
	
	
	private static String isRESTorRPC() {
		HttpServletRequest request = getRequest();
		return BaseResultFactory.isNotNull(request) ? " Dubbo RPC Error Not Request ":request.getRequestURL().toString();
	}

}
