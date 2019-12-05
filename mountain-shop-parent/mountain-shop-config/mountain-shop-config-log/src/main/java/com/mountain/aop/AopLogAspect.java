
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

package com.mountain.aop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;

import org.apache.dubbo.rpc.RpcException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.mountain.factory.BaseResultFactory;
//import com.mountain.kafka.KafkaSender;
import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.factory.result.error.ErrorResult;
import com.mountain.tool.UserAgentUtils;

/**
 * 
 * 	功能说明：日志收集
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Aspect
@Component
public class AopLogAspect extends BaseResultFactory {
//	@Autowired
//	private KafkaSender<JSONObject> kafkaSender;
	
	public static AtomicLong groupId = new AtomicLong(0);  
	
	public static ConcurrentHashMap<Long, String> adminHealthError=new ConcurrentHashMap<Long, String>();
 
	@Pointcut("execution(* com.mountain.*.api.impl.*.*(..))")
	private void serviceAspect() {
	}

	@Around("serviceAspect()")
	public AbstractBaseResult doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		Long startTime = System.currentTimeMillis();
		
		Long id = groupId.incrementAndGet();
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		StringBuilder printLog=new StringBuilder();
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("############### 日志编号 ："+id+" Begin ##################"+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		printLog.append("                                             "+"\r\n");
		
		
		
		
		if (!isNotNull(requestAttributes)) {

			HttpServletRequest request = requestAttributes.getRequest();

			// 打印请求内容
			
			printLog.append("=============== Request Begin ==============="+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问时间 ：" + new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date())+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问IP ：" + UserAgentUtils.getIpAddr(request) +"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问地址 ：" + request.getRequestURL().toString()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问方式 ：" + request.getMethod()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问参数 ：" + request.getQueryString()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问类地址 ：" + proceedingJoinPoint.getSignature().getDeclaringTypeName()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问类方法 ：" + proceedingJoinPoint.getSignature().getName()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("访问类方法参数 ：" + Arrays.toString(proceedingJoinPoint.getArgs())+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("=============== Request End ==============="+"\r\n");
			printLog.append("                                           "+"\r\n");
			printLog.append("                                           "+"\r\n");
			printLog.append("                                           "+"\r\n");
		} else {
			printLog.append("=============== Dubbo RPC Begin ==============="+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("请求时间 ：" + new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date())+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("请求类地址 ：" + proceedingJoinPoint.getSignature().getDeclaringTypeName()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("请求类方法 ：" + proceedingJoinPoint.getSignature().getName()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("请求类方法参数 ：" + Arrays.toString(proceedingJoinPoint.getArgs())+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("=============== Dubbo RPC End ==============="+"\r\n");
			printLog.append("                                               "+"\r\n");
			printLog.append("                                               "+"\r\n");
			printLog.append("                                               "+"\r\n");
		}

		AbstractBaseResult<?> resultData = null;
		StackTraceElement stackTraceElement=null;
		String returnDate = null;
		try {
			resultData = (AbstractBaseResult<?>) proceedingJoinPoint.proceed();
			returnDate = new SimpleDateFormat("mm:ss").format(System.currentTimeMillis() - startTime);
		} catch (Throwable e) {
			stackTraceElement= e.getStackTrace()[0];
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(!adminHealthError.values().contains(stackTraceElement.toString())) {
				adminHealthError.put(id, stackTraceElement.toString());
			}
			printLog.append("                                             "+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("===============  Error	"+(isNotNull(requestAttributes) ? "RPC":"")+"  Begin   ==============="+"\r\n");
			
			
			if(e instanceof RpcException) {
				RpcException rpcError = ((RpcException) e);
				String errorMessage=rpcError.getLocalizedMessage().split("com.mountain.")[1];
				resultData = error(code(6).addMessage((code(6).getMessage().lastIndexOf("(")!=-1?"":"("+errorMessage.substring(0,errorMessage.indexOf("."))+")")));
			}else {
				resultData = error(code(500));
			}
			
			
			if(!isNotNull(authentication)) {
				printLog.append("                                             "+"\r\n");
				printLog.append("触发异常用户："+authentication.getName()+"\r\n");
			}
			printLog.append("                                             "+"\r\n");
			printLog.append("异常错误 ：" + e.getLocalizedMessage()+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("异常地址 ："+stackTraceElement+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("===============  Error	 "+(isNotNull(requestAttributes) ? "RPC":"")+"  End   ================"+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("                                             "+"\r\n");
			return resultData;
		} finally {
			printLog.append("                                              "+"\r\n");
			printLog.append("                                              "+"\r\n");
			printLog.append("                                              "+"\r\n");
			printLog.append("=============== Response "+(isNotNull(requestAttributes) ? "RPC":"")+" Begin ==============="+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("响应时间 ：" + new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date())+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("响应用时 ：" + returnDate +"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("响应数据 ：" + resultData+"\r\n");
			printLog.append("                                             "+"\r\n");
			printLog.append("=============== Response "+(isNotNull(requestAttributes) ? "RPC":"")+" End ==============="+"\r\n");
			printLog.append("                                            "+"\r\n");
			printLog.append("                                            "+"\r\n");
			printLog.append("                                            "+"\r\n");
			//要想办法添加链路日志     彭晋龙     2019/10/15 5:44:00
			printLog.append("############### 日志编号 ："+id+" End ##################"+"\r\n");
			
			if(resultData.ifStatus()) {
				System.out.println(printLog);
			}else {
				System.err.println(printLog);
				ErrorResult errorResult = (ErrorResult) resultData;
				if(resultData.getCode()!=200 && isNotNull(errorResult.getId())) {
					errorResult.setId(id);
					return errorResult;
				}
			}
			
		}
		
		
		return resultData;

	}
	
	
}
