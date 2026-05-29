
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

package com.maozi.log.config;

import cn.hutool.core.util.StrUtil;
import com.maozi.base.enums.LogCommonType;
import com.maozi.common.LogUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.constant.LogTag;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class RemoteInvokeErrorAop {

	private static final String IMPL = "impl";

	private final String RPC_POINT = ApplicationEnvironmentContext.PACKAGE_PREFIX + ".common.result.AbstractBaseResult " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.rpc..*.*(..)";

	private final String REST_POINT = ApplicationEnvironmentContext.PACKAGE_PREFIX + ".common.result.AbstractBaseResult " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.rest..*.*(..)";

	private final String POINT = "execution(" + RPC_POINT + ") || execution(" + REST_POINT + ")";

	@Around(POINT)
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		try {return proceedingJoinPoint.proceed();} catch (Throwable e) {

			String [] remoteInvokeClassSplit = proceedingJoinPoint.getSignature().getDeclaringTypeName().split("\\.");

			if(!IMPL.equals(remoteInvokeClassSplit[5])){

				String serviceName = remoteInvokeClassSplit[2];

				Map<String, String> logs = new LinkedHashMap<>();

				logs.put(LogTag.TYPE, LogCommonType.RPC.getDesc());
				logs.put(LogTag.SERVICE_NAME, serviceName);
				logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature().getDeclaringTypeName() + ":" + proceedingJoinPoint.getSignature().getName());
				logs.put(LogTag.PARAM, Arrays.toString(proceedingJoinPoint.getArgs()));
				logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());
				logs.put(LogTag.ERROR_LINE, e.getStackTrace()[0].toString());

				LogUtil.error(log,e);

				LogUtil.error(log,logs);

				ErrorCode errorCode = SystemErrorCode.SERVICE_RPC_ERROR;
				return ResultUtil.error(errorCode)
						.setExceptionMessage(StrUtil.upperFirst(serviceName) + errorCode.getExceptionMessage())
						.setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);

			}

			throw e;

		}

    }

}