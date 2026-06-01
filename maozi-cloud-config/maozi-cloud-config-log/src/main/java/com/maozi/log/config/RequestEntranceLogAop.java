
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

import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.Node;
import com.maozi.base.enums.EnvironmentType;
import com.maozi.base.utils.EnvironmentUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.constant.LogTag;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.log.utils.RestEntranceLogUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1 )
public class RequestEntranceLogAop {

	private final String RPC_POINT = "* " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.impl.rpc..*(..)";

	private final String REST_POINT = "* " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.impl.rest..*(..)";

	private final String BASE_RPC_POINT = ApplicationEnvironmentContext.PACKAGE_PREFIX + ".common.result.AbstractBaseResult " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".base.api.impl.BaseServiceImpl.*(..)";

	private final String POINT = "execution(" + RPC_POINT + ") || execution(" + REST_POINT + ") || execution( " + BASE_RPC_POINT + " )";

	@Resource
	private RestEntranceLogUtils restEntranceLogUtils;

    @Around(POINT)
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint){

    	long startTime = System.currentTimeMillis();

    	HttpServletRequest request = WebUtil.getRequest();

    	RpcContext rpcContext = RpcContext.getServiceContext();

    	String rpcUrl = rpcContext.getLocalHost();

    	String param = Arrays.toString(proceedingJoinPoint.getArgs());

    	Node curNode = ContextUtil.getContext().getCurNode();

    	Map<String, String> logs = restEntranceLogUtils.requestLog(proceedingJoinPoint, request, rpcUrl);

		if(EnvironmentUtil.notEnvironment(EnvironmentType.PROD)){
			logs.put(LogTag.PARAM, param);
		}

        Object resultData = null;

        try {
			resultData = proceedingJoinPoint.proceed();
		}catch (AccessDeniedException e) {
			throw e;
        }catch (BusinessResultException businessResultException) {
        	resultData = businessResultException.getErrorResult();
    	}catch (Throwable e) {

            resultData = ResultUtil.error(SystemErrorCode.SYSTEM_ERROR).setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);

            LogUtil.error(log,e);

            logs.put(LogTag.PARAM, param);
            logs.put(LogTag.ERROR_USER,ApplicationLinkContext.USERNAMES.get());
            logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());

            StackTraceElement[] errorLines = e.getStackTrace();
            if(errorLines.length > 0) {
            	logs.put(LogTag.ERROR_LINE, errorLines[0].toString());
            }

        } finally {

			StringBuilder sqlLog = LogUtil.sqlLog.get();
			if(ObjectUtil.isNotNullEmpty(sqlLog)) {logs.put(LogTag.SQL, sqlLog.toString());}

			logs.put(LogTag.RT, (System.currentTimeMillis() - startTime) + " ms");

        	if(ObjectUtil.isNotNullEmpty(resultData)) {

				if(resultData instanceof AbstractBaseResult<?> result){

					if(!result.isSuccess()) {

						curNode.increaseBlockQps(1);

						ErrorResult<?> errorResult = result.getErrorResult();

						if(errorResult.autoIdentifyHttpCode().isBusinessError()) {log.warn(LogUtil.convertLog(logs));}

						else {LogUtil.error(log,logs);}

						return result;

					}

				}

        	}

			curNode.addPassRequest(1);

			LogUtil.info(log,logs);

		}

		if(ObjectUtil.isNullEmpty(request)) {ApplicationLinkContext.clearContext();}

        return resultData;

    }

}
