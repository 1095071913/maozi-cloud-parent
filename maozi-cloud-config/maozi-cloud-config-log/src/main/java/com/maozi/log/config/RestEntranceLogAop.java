
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
import com.maozi.base.error.code.SystemErrorCode;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.log.utils.RestEntranceLogUtils;
import com.maozi.utils.constant.LogTag;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1 )
public class RestEntranceLogAop extends BaseCommon {
	
	@Resource
	private RestEntranceLogUtils restEntranceLogUtils;

	private final String POINT = "execution(* com.maozi.*.*.api.impl.rpc..*(..)) || execution(* com.maozi.*.*.api.impl.rest..*(..)) || execution(com.maozi.common.result.AbstractBaseResult com.maozi.base.api.impl.BaseServiceImpl.*(..))";

    @Around(POINT)
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint){

    	long startTime = System.currentTimeMillis();
    	
    	String tid = BaseCommon.getTraceId();
    	
    	HttpServletRequest request = getRequest();
    	
    	RpcContext rpcContext = RpcContext.getServiceContext();
    	
    	String rpcUrl = rpcContext.getRemoteHost();
    	
    	String param = Arrays.toString(proceedingJoinPoint.getArgs());
    	
    	Node curNode = ContextUtil.getContext().getCurNode();
        
    	Map<String, String> logs = restEntranceLogUtils.logRequest(proceedingJoinPoint, request, rpcUrl);
    	
        functionParam(param);
        
		if(notEnvironment(EnvironmentType.PROD)){
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

        	String stackTrace = getStackTrace(e);
        	
            resultData = error(SystemErrorCode.SYSTEM_ERROR,500);
            
            functionError(stackTrace);
            
            log.error(stackTrace);
            
            logs.put(LogTag.PARAM, param);
            logs.put(LogTag.ERROR_USER,getCurrentUserName());
            logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());
            
            StackTraceElement[] errorLines = e.getStackTrace();
            if(errorLines.length > 0) {
            	logs.put(LogTag.ERROR_LINE, errorLines[0].toString());
            }
            
        } finally {

			StringBuilder respSql = sql.get();
			if(isNotNull(respSql)) {logs.put(LogTag.SQL, respSql.toString());}

			logs.put(LogTag.RT, (System.currentTimeMillis() - startTime) + " ms");
        	
        	if(isNotNull(resultData)) {
                
                functionReturn(resultData);

				if(resultData instanceof AbstractBaseResult<?> result){

					if(!result.isSuccess()) {

						curNode.increaseBlockQps(1);

						ErrorResult<?> errorResult = result.getResult();

						if(errorResult.autoIdentifyHttpCode().isBusinessError()) {log.warn(appendLog(logs).toString());}

						else {error(logs);}

						if(result.getCode() == 500 && logs.containsKey(LogTag.ERROR_DESC)) {restEntranceLogUtils.errorLogAlarm(proceedingJoinPoint,param,tid,logs);}

						return result;

					}

				}
        		
        	}

			curNode.addPassRequest(1);

			info(logs);

		}

		if(isNull(request)) {BaseCommon.clearContext();}
        
        return resultData;
    
    }

}
