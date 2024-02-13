package com.maozi.log.utils;

import com.maozi.common.BaseCommon;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RestEntranceLogUtils extends BaseCommon {

	public Map<String, String> logRequest(ProceedingJoinPoint proceedingJoinPoint,HttpServletRequest request,String rpcUrl) {
    	
		Map<String, String> logs = new LinkedHashMap();

        if (!isNull(request)) {

        	logs.put("Type", "Undertow");
        	logs.put("IP", getIpAddr(request));
            logs.put("URI", request.getRequestURL().toString());
            logs.put("Function", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        } else {
            
        	logs.put("Type", "Dubbo");
        	logs.put("IP", rpcUrl);
        	logs.put("Function", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        }
        
        return logs;
    }
    
    
    @Async("applicationTaskExecutor")
    public void errorLogAlarm(ProceedingJoinPoint proceedingJoinPoint,String arg,String tid,Map<String,String> logs) {
    	
    	String errorUrl=logs.get("ErrorLine");
    	
    	String key=proceedingJoinPoint.getSignature().getDeclaringTypeName()+proceedingJoinPoint.getSignature().getName()+errorUrl;
    	
    	if(!adminHealthError.containsKey(key)) { 
    		errorAlarm(key, logs);
    	}
    	
    }
    
    public static String getIpAddr(HttpServletRequest request) {
    	
    	String ip = request.getHeader("x-forwarded-for");
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip;
	}
	
}
