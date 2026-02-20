package com.maozi.feign.config;

import com.maozi.utils.context.ApplicationLinkContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import static com.maozi.common.BaseCommon.getRequest;

@Component
public class TokenRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		
		HttpServletRequest request = getRequest();
		
		Enumeration<String> headerNames = request.getHeaderNames();
		
		if (headerNames != null) {

			while (headerNames.hasMoreElements()) {
				
				String name = headerNames.nextElement();
				
				if("authorization".equals(name)) {
					requestTemplate.header(name, request.getHeader(name));
				}
				
			}

			requestTemplate.header(ApplicationLinkContext.VERSION, ApplicationLinkContext.VERSIONS.get());

		}
		
	}

}