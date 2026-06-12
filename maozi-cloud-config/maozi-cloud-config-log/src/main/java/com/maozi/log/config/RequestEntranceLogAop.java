
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

/**
 * 请求入口日志切面
 * <p>
 * 拦截 REST 接口、RPC 接口和基础服务实现类的请求入口，记录请求日志、
 * 响应日志和异常日志。集成 Sentinel 进行流量统计（通过 QPS 和阻塞 QPS），
 * 并在非生产环境下记录请求参数。异常发生时根据错误类型自动分类记录
 * （业务错误 warn 级别、系统错误 error 级别）。
 * </p>
 *
 * @author maozi
 */
@Slf4j
@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1 )
public class RequestEntranceLogAop {

	/** RPC 接口切点表达式 */
	private final String RPC_POINT = "* " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.impl.rpc..*(..)";

	/** REST 接口切点表达式 */
	private final String REST_POINT = "* " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.impl.rest..*(..)";

	/** 基础服务实现类切点表达式 */
	private final String BASE_RPC_POINT = ApplicationEnvironmentContext.PACKAGE_PREFIX + ".common.result.AbstractBaseResult " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".base.api.impl.BaseServiceImpl.*(..)";

	/** 组合切点表达式 */
	private final String POINT = "execution(" + RPC_POINT + ") || execution(" + REST_POINT + ") || execution( " + BASE_RPC_POINT + " )";

	/** REST 入口日志工具 */
	@Resource
	private RestEntranceLogUtils restEntranceLogUtils;

	/**
	 * 环绕通知，记录请求日志、执行业务逻辑并记录响应日志
	 * <p>
	 * 执行流程：
	 * <ol>
	 *   <li>记录请求开始时间和基本信息</li>
	 *   <li>执行业务方法</li>
	 *   <li>捕获异常并转换为统一错误结果</li>
	 *   <li>在 finally 块中记录 SQL 日志、响应时间和 Sentinel 统计</li>
	 * </ol>
	 * </p>
	 *
	 * @param proceedingJoinPoint AOP 连接点
	 * @return 业务方法执行结果
	 */
    @Around(POINT)
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint){

    	/** 请求开始时间戳 */
    	long startTime = System.currentTimeMillis();

    	/** 当前 HTTP 请求 */
    	HttpServletRequest request = WebUtil.getRequest();

    	/** Dubbo RPC 上下文 */
    	RpcContext rpcContext = RpcContext.getServiceContext();

    	/** RPC 服务地址 */
    	String rpcUrl = rpcContext.getLocalHost();

    	/** 方法参数字符串 */
    	String param = Arrays.toString(proceedingJoinPoint.getArgs());

    	/** Sentinel 当前节点 */
    	Node curNode = ContextUtil.getContext().getCurNode();

    	/** 日志信息集合 */
    	Map<String, String> logs = restEntranceLogUtils.requestLog(proceedingJoinPoint, request, rpcUrl);

    	// 非生产环境记录请求参数
		if(EnvironmentUtil.notEnvironment(EnvironmentType.PROD)){
			logs.put(LogTag.PARAM, param);
		}

        // 方法返回结果，初始为 null
        Object resultData = null;

        try {
        	// 执行目标业务方法
			resultData = proceedingJoinPoint.proceed();

		// 权限拒绝异常：不封装结果，直接向上抛出由 Spring Security 处理
		}catch (AccessDeniedException e) {
			throw e;

		// 业务异常：封装为业务错误结果（已包含错误码和错误信息），不记录 error 日志
        }catch (BusinessResultException businessResultException) {
        	resultData = businessResultException.getErrorResult();

    	// 未预期的系统异常：封装为统一的系统错误结果，并记录详细的错误日志
    	}catch (Throwable e) {

    		// 构建系统级错误响应
            resultData = ResultUtil.error(SystemErrorCode.SYSTEM_ERROR).setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);

            // 记录异常堆栈日志，便于排查问题
            LogUtil.error(log,e);

            // 收集系统错误相关的日志信息
            logs.put(LogTag.PARAM, param);                                       // 记录请求参数（系统异常时无论环境均记录）
            logs.put(LogTag.ERROR_USER,ApplicationLinkContext.USERNAMES.get());   // 记录当前操作用户
            logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());                 // 记录异常描述

            // 记录异常发生的第一行代码位置
            StackTraceElement[] errorLines = e.getStackTrace();
            if(errorLines.length > 0) {
            	logs.put(LogTag.ERROR_LINE, errorLines[0].toString());
            }

        } finally {

        	// 记录本次请求过程中执行的 SQL 日志（从 ThreadLocal 中获取）
			StringBuilder sqlLog = LogUtil.sqlLog.get();
			if(ObjectUtil.isNotNullEmpty(sqlLog)) {logs.put(LogTag.SQL, sqlLog.toString());}

			// 记录响应时间
			logs.put(LogTag.RT, (System.currentTimeMillis() - startTime) + " ms");

        	if(ObjectUtil.isNotNullEmpty(resultData)) {

        		// 判断结果是否为框架统一响应类型
				if(resultData instanceof AbstractBaseResult<?> result){

					// 请求失败（业务或系统错误）
					if(!result.isSuccess()) {

						// 统计 Sentinel 阻塞 QPS（表示请求被限流或熔断）
						curNode.increaseBlockQps(1);

						// 获取错误详情，用于判断是业务错误还是系统错误
						ErrorResult<?> errorResult = result.getErrorResult();

						// 业务错误使用 warn 级别，系统错误使用 error 级别
						if(errorResult.autoIdentifyHttpCode().isBusinessError()) {log.warn(LogUtil.convertLog(logs));}

						else {LogUtil.error(log,logs);}

						return result;

					}

				}

        	}

			// 统计 Sentinel 通过 QPS（表示请求成功处理）
			curNode.addPassRequest(1);

			// 请求成功，记录 info 级别日志
			LogUtil.info(log,logs);

		}

		// 非 HTTP 请求时（RPC 调用）清理上下文
		if(ObjectUtil.isNullEmpty(request)) {ApplicationLinkContext.clearContext();}

        return resultData;

    }

}
