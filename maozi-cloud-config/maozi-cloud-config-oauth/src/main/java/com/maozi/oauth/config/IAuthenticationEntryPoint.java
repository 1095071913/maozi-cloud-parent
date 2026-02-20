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

import com.maozi.base.CodeData;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.utils.MapperUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IAuthenticationEntryPoint extends BaseCommon implements AuthenticationEntryPoint {

	private static final int NO_AUTHENTICATION_CODE = 401;

	private static final String NO_AUTHENTICATION_DEFAULT_MESSAGE = "Full authentication is required to access this resource";
	
	@Override
	public void commence(HttpServletRequest request,HttpServletResponse response,AuthenticationException authException) {

		String errorMessage = NO_AUTHENTICATION_DEFAULT_MESSAGE.equals(authException.getMessage()) ? "未认证授权" : authException.getMessage();

		AbstractBaseResult<Void> error = error(new CodeData<Void>(NO_AUTHENTICATION_CODE,errorMessage),NO_AUTHENTICATION_CODE).autoIdentifyHttpCode();
			
		MapperUtils.setResponseBody(response,error);
		
	}
	
}