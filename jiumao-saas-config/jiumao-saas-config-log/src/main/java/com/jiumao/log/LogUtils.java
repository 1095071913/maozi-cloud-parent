package com.jiumao.log;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

import com.jiumao.factory.BaseResultFactory;

public class LogUtils extends BaseResultFactory{

	public Map<String, String> logRequest(ProceedingJoinPoint proceedingJoinPoint,HttpServletRequest request,String rpcUrl) {
    	
		Map<String, String> logs = new LinkedHashMap();

        if (!isNull(request)) {

        	logs.put("reqType", "web-mvc");
        	logs.put("reqIp", getIpAddr(request)+" net");
            logs.put("reqUrl", request.getRequestURL().toString());
            logs.put("reqFunc", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        } else {
            
        	logs.put("reqType", "dubbo");
        	logs.put("reqIp", rpcUrl+" net");
        	logs.put("reqFunc", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        }
        
        return logs;
    }
    
    
    @Async("applicationTaskExecutor")
    public void errorLogAlarm(ProceedingJoinPoint proceedingJoinPoint,String arg,String tid,Map<String,String> logs) {
    	
    	String errorUrl=logs.get("errorLine");
    	
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
