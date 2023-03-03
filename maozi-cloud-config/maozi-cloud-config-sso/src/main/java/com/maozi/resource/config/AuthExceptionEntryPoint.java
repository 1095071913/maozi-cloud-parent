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

package com.maozi.resource.config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.maozi.factory.BaseResultFactory;
import com.maozi.factory.result.AbstractBaseResult;
import com.maozi.factory.result.code.CodeAttribute;
import com.maozi.tool.MapperUtils;


/**
 * 
 * 	功能说明：Token错误信息
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


public class AuthExceptionEntryPoint extends BaseResultFactory implements AuthenticationEntryPoint {
	
	//Token错误显示
	private Integer errorCode=401;
	
	@Override
	public void commence(HttpServletRequest request,HttpServletResponse response,AuthenticationException authException) throws ServletException {
		
		try {  
			
			String errorMessage=authException.getMessage();  
			
			String[] errorMessageArray = errorMessage.split("\\|"); 
			
			if("Full authentication is required to access this resource".equals(errorMessage)) {
				errorMessage="未携带Token"; 
			}  
			
			AbstractBaseResult error = error(new CodeAttribute(401,errorMessageArray.length==1?errorMessage:errorMessageArray[1]),errorCode).autoIdentifyHttpCode();
			
			MapperUtils.getInstance().writeValue(response.getOutputStream(), error);    
			
		} catch (Exception e) { log.error(getStackTrace(e));throw new ServletException(); }
		
	}
	
}