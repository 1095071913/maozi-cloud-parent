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

package com.maozi.oauth.config;

import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.code.CodeAttribute;
import com.maozi.utils.MapperUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class IAuthenticationEntryPoint extends BaseCommon implements AuthenticationEntryPoint {
	
	private Integer errorCode=401;
	
	@Override
	public void commence(HttpServletRequest request,HttpServletResponse response,AuthenticationException authException) throws ServletException {
		
		try {  
			
			String errorMessage = "Full authentication is required to access this resource".equals(authException.getMessage()) ? "未携带Token" : authException.getMessage();

			AbstractBaseResult error = error(new CodeAttribute(401,errorMessage),errorCode).autoIdentifyHttpCode();
			
			MapperUtils.getInstance().writeValue(response.getOutputStream(),error);
			
		} catch (Exception e) {

			log.error(getStackTrace(e));

			throw new ServletException();

		}
		
	}
	
}