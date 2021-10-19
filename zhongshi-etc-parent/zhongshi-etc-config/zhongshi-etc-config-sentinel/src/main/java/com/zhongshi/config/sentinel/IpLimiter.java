package com.zhongshi.config.sentinel;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;

@Component
public class IpLimiter implements RequestOriginParser {

	@Override
	public String parseOrigin(HttpServletRequest request) {
		return getIpAddress(request);
	}

	public static String getIpAddress(HttpServletRequest request) {
		
		String ip = request.getHeader("x-forwarded-for");
		
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
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
		return ip;
	}

}