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

import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,HttpServletResponse response,AuthenticationException authException) {

		Throwable causeException = authException.getCause();
		AbstractBaseResult<?> errorResult = ObjectUtil.isNotNullEmpty(causeException) && causeException.getCause() instanceof BusinessResultException businessResultException ?
				businessResultException.getErrorResult()
				:
				ResultUtil.error(SystemErrorCode.USER_AUTH_ERROR).autoIdentifyHttpCode(SystemErrorCode.USER_AUTH_ERROR_DEFAULT_CODE);

		WebUtil.writeResponseBody(response,errorResult);

	}
	
}