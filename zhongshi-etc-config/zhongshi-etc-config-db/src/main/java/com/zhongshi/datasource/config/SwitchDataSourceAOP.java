package com.zhongshi.datasource.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Lazy(false)
@Order(0)
public class SwitchDataSourceAOP {
	
	
	@Before("execution(* com.zhongshi.*.api.impl.*.*(..)) || execution(* com.zhongshi.*.api.impl.*.*.*(..))")
	public void process(JoinPoint joinPoint) {
		
		String methodName = joinPoint.getSignature().getName();
		
		if (methodName.startsWith("insert") || methodName.startsWith("delete") || methodName.startsWith("update")) {
			DataSourceContextHolder.setDbType("update-datasource");
		} else {
			DataSourceContextHolder.setDbType("select-datasource");
		}
			
	}
}