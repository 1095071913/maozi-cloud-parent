package com.maozi.log.utils;

import com.maozi.base.enums.LogCommonType;
import com.maozi.common.ObjectUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.constant.LogTag;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestEntranceLogUtils{

	public Map<String, String> requestLog(ProceedingJoinPoint proceedingJoinPoint,HttpServletRequest request,String rpcUrl) {
    	
		Map<String, String> logs = new LinkedHashMap<>();

		boolean isHttp = ObjectUtil.isNotNullEmpty(request);
		logs.put(LogTag.TYPE, isHttp ? LogCommonType.WEB.getDesc() : LogCommonType.RPC.getDesc());
		logs.put(LogTag.IP, isHttp ? WebUtil.getRequestHost(request) : rpcUrl);
		if(isHttp){
			logs.put(LogTag.URL, request.getRequestURL().toString());
		}
		logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        return logs;

    }
	
}
