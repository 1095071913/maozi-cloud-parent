package com.jiumao.feign;
//package com.zhongshi.feign;
//
//import static com.zhongshi.factory.BaseResultFactory.getRequest;
//import java.util.Enumeration;
//import javax.servlet.http.HttpServletRequest;
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//
//public class FeignRequestInterceptor implements RequestInterceptor {
//
//	@Override
//	public void apply(RequestTemplate requestTemplate) {
//		
//		HttpServletRequest request = getRequest();
//		
//		Enumeration<String> headerNames = request.getHeaderNames();
//		if (headerNames != null) {
//			while (headerNames.hasMoreElements()) {
//				String name = headerNames.nextElement();
//				String value = request.getHeader(name);
//				requestTemplate.header(name, value);
//			}
//		}
//		
//		
//		Enumeration<String> parameterNames = request.getParameterNames();
//		if (parameterNames != null) {
//			while (parameterNames.hasMoreElements()) {
//				String name = parameterNames.nextElement();
//				String value = request.getParameter(name);
//				if ("access_token".equals(name)) {
//					requestTemplate.header("authorization", "Bearer " + value);
//				}
//			}
//		}
//		
//		
//		
//		
//		
//		
//	}
//
//}
