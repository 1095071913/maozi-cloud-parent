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

package com.jiumao.resource.config;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.jiumao.factory.BaseResultFactory;
import com.jiumao.tool.MapperUtils;


/**
 * 
 * 	功能说明：SSO权限验证设置错误信息
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


public class CustomAccessDeniedHandler extends BaseResultFactory implements AccessDeniedHandler {

	//权限错误显示
	private Integer errorCode=403;
	
    @Override 
    public void handle(HttpServletRequest request, HttpServletResponse response,AccessDeniedException accessDeniedException)throws IOException, ServletException {
    	try {
			MapperUtils.getInstance().writeValue(response.getOutputStream(), error(code(errorCode),errorCode).autoIdentifyHttpCode());
		} catch (Exception e) {
			log.error(getStackTrace(e));
			throw new ServletException();
		}
    }
}