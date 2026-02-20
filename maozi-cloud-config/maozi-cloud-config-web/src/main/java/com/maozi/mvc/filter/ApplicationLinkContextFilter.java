package com.maozi.mvc.filter;

import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationLinkContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class ApplicationLinkContextFilter implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = (BaseCommon.isNull(authentication) || !authentication.isAuthenticated()) ? null : authentication.getName();
		ApplicationLinkContext.USERNAMES.set(username);

		String version = BaseCommon.getVersionDefault(request.getHeader(ApplicationLinkContext.VERSION));
		ApplicationLinkContext.VERSIONS.set(version);

		return true;

	}

	@Override
	public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
		BaseCommon.clearContext();
	}

}