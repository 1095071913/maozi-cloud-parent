package com.maozi.log.utils;

import com.maozi.base.enums.LogCommonType;
import com.maozi.common.BaseCommon;
import com.maozi.utils.constant.LogTag;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestEntranceLogUtils extends BaseCommon {

	public Map<String, String> logRequest(ProceedingJoinPoint proceedingJoinPoint,HttpServletRequest request,String rpcUrl) {
    	
		Map<String, String> logs = new LinkedHashMap<>();

		boolean isHttp = isNotNull(request);
		logs.put(LogTag.TYPE, isHttp ? LogCommonType.WEB.getDesc() : LogCommonType.RPC.getDesc());
		logs.put(LogTag.IP, isHttp ? getIpAddr(request) : rpcUrl);
		if(isHttp){
			logs.put(LogTag.URL, request.getRequestURL().toString());
		}
		logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        return logs;

    }
    
    
    @Async("applicationTaskExecutor")
    public void errorLogAlarm(ProceedingJoinPoint proceedingJoinPoint,String arg,String tid,Map<String,String> logs) {
    	
    	String errorLine = logs.get(LogTag.ERROR_LINE);
    	
    	String key = proceedingJoinPoint.getSignature().getDeclaringTypeName() + proceedingJoinPoint.getSignature().getName() + errorLine;
    	
    	if(!adminHealthError.containsKey(key)) { 
    		errorAlarm(key, logs);
    	}
    	
    }
    
    public static String getIpAddr(HttpServletRequest request) {
    	
    	String ip = request.getHeader("x-forwarded-for");
		
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip;
	}
	
}
