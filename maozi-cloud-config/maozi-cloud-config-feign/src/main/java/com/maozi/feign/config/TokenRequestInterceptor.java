package com.maozi.feign.config;

import com.maozi.common.WebUtil;
import com.maozi.common.context.ApplicationLinkContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

@Component
public class TokenRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		
		HttpServletRequest request = WebUtil.getRequest();

        Enumeration<String> headerNames = request != null ? request.getHeaderNames() : null;
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