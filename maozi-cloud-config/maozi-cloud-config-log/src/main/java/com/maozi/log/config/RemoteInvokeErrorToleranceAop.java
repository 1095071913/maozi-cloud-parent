
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

import com.maozi.common.BaseCommon;
import com.maozi.common.result.code.CodeAttribute;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 功能说明：日志收集
 * <p>
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * <p>
 * 创建日期：2019-08-03 ：1:32:00
 * <p>
 * 版权归属：蓝河团队
 * <p>
 * 协议说明：Apache2.0（ 文件顶端 ）
 */ 

@Aspect
@Component
public class RemoteInvokeErrorToleranceAop extends BaseCommon {

	private final String POINT = "execution(com.maozi.common.result.AbstractBaseResult com.maozi.*.*.api.rpc..*.*(..)) || execution(com.maozi.common.result.AbstractBaseResult com.maozi.*.*.api.rest..*.*(..))";

	@Around(POINT)
	public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		try {return proceedingJoinPoint.proceed();} catch (Throwable e) {

			String [] remoteInvokeClassSplit = proceedingJoinPoint.getSignature().getDeclaringTypeName().split("\\.");

			if(!"impl".equals(remoteInvokeClassSplit[5])){

				String applicationName = remoteInvokeClassSplit[2];

				Map<String, String> logs = new LinkedHashMap<String, String>();

				logs.put("Type", "RemoteInvoke");
				logs.put("Name", applicationName);
				logs.put("Function", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());
				logs.put("ErrorParam", Arrays.toString(proceedingJoinPoint.getArgs()));
				logs.put("ErrorDesc", e.getLocalizedMessage());
				logs.put("ErrorLine", e.getStackTrace()[0].toString());

				log.error(getStackTrace(e));

				log.error(appendLog(logs).toString());

				return error(new CodeAttribute(6,"服务错误" + "(" + applicationName + ")"),500);

			}

			throw e;

		}

    }

}