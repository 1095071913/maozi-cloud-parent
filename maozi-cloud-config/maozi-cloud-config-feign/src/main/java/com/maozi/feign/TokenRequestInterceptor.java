package com.maozi.feign;

import static com.maozi.common.BaseCommon.getRequest;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class TokenRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		
		HttpServletRequest request = getRequest();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				
				String name = headerNames.nextElement();
				
				if(name.equals("authorization")) {
					requestTemplate.header("authorization", request.getHeader(name));
				}
				
			}
		}
		
//		Enumeration<String> parameterNames = request.getParameterNames();
//		if (parameterNames != null) {
//			while (parameterNames.hasMoreElements()) {
//				String name = parameterNames.nextElement();
//				if ("access_token".equals(name)) {
//					requestTemplate.header("authorization", request.getParameter(name));
//				}
//			}
//		}
		
	}

}
