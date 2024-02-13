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
import com.maozi.utils.MapperUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class IAccessDeniedHandler extends BaseCommon implements AccessDeniedHandler {

	//权限错误显示
	private Integer errorCode=403;
	
    @Override 
    public void handle(HttpServletRequest request, HttpServletResponse response,AccessDeniedException accessDeniedException) throws IOException, ServletException {

		try {MapperUtils.getInstance().writeValue(response.getOutputStream(), error(code(errorCode),errorCode).autoIdentifyHttpCode());} catch (Exception e) {

			log.error(getStackTrace(e));

			throw new ServletException();

		}

    }

}