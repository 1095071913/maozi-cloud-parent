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

import com.maozi.common.ResultUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class IAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final int AUTHENTICATION_ERROR_CODE = 401;

	private static final String NO_AUTHENTICATION_DEFAULT_MESSAGE = "Full authentication is required to access this resource";

	private static final ErrorCode NO_AUTHENTICATION_CODE_DATA = new ErrorCode(AUTHENTICATION_ERROR_CODE,"用户未认证授权");

	private static final ErrorCode ERROR_AUTHENTICATION_CODE_DATA = new ErrorCode(AUTHENTICATION_ERROR_CODE,"用户认证授权失败");

	@Override
	public void commence(HttpServletRequest request,HttpServletResponse response,AuthenticationException authException) {

		ErrorCode errorCode = NO_AUTHENTICATION_DEFAULT_MESSAGE.equals(authException.getMessage()) ? NO_AUTHENTICATION_CODE_DATA : ERROR_AUTHENTICATION_CODE_DATA;

		AbstractBaseResult<Object> error = ResultUtil.error(errorCode).autoIdentifyHttpCode(AUTHENTICATION_ERROR_CODE);
			
		WebUtil.writeResponseBody(response,error);
		
	}
	
}