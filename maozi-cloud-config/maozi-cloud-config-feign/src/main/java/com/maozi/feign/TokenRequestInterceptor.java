package com.maozi.feign;

import static com.maozi.common.BaseCommon.getRequest;

import com.maozi.utils.context.ApplicationLinkContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

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

			requestTemplate.header("Version", ApplicationLinkContext.VERSIONS.get());

		}
		
	}

}