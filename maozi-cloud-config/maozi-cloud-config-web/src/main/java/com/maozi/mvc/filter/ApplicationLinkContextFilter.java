package com.maozi.mvc.filter;

import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 应用链路上下文过滤器
 * <p>
 * 在 HTTP 请求处理前从 Spring Security 上下文中提取当前用户名，
 * 从请求头中提取版本号，存入 {@link ApplicationLinkContext} 线程本地变量中。
 * 请求完成后自动清理上下文，防止线程池复用导致的数据泄漏。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class ApplicationLinkContextFilter implements HandlerInterceptor {

    /**
     * 请求处理前设置链路上下文
     * <p>
     * 从 Security 上下文中获取当前认证用户名，从请求头获取版本号，
     * 分别存入线程本地变量。
     * </p>
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param handler 处理器
     * @return 始终返回 true，继续执行后续拦截器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (ObjectUtil.isNullEmpty(authentication) || !authentication.isAuthenticated()) ? null : authentication.getName();
        ApplicationLinkContext.USERNAMES.set(username);

        String version = ApplicationLinkContext.getVersionDefault(request.getHeader(ApplicationLinkContext.VERSION));
        ApplicationLinkContext.VERSIONS.set(version);

        return true;

    }

    /**
     * 请求完成后清理链路上下文
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param handler 处理器
     * @param ex 处理过程中可能抛出的异常
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        ApplicationLinkContext.clearContext();
    }

}
