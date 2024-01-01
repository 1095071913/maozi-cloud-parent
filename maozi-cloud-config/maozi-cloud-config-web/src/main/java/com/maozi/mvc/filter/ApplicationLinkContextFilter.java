package com.maozi.mvc.filter;

import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationLinkContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class ApplicationLinkContextFilter implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = (BaseCommon.isNull(authentication) || !authentication.isAuthenticated()) ? "anonymousUser" : authentication.getName();

		ApplicationLinkContext.set(request.getHeader("Version"),username);

		return true;

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable Exception ex) throws Exception {
		BaseCommon.clearContext();
	}

}