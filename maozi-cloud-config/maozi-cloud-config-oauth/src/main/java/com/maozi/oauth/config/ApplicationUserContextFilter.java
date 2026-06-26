package com.maozi.oauth.config;

import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.dto.CurrentUserInfo;
import com.maozi.oauth.token.constants.OAuth2TokenClaimConstants;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

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
public class ApplicationUserContextFilter implements HandlerInterceptor {

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
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(ObjectUtil.isNotNullEmpty(authentication) && authentication.isAuthenticated() && authentication.getPrincipal() instanceof DefaultOAuth2AuthenticatedPrincipal authenticatedPrincipal){
            CurrentUserInfo currentUserInfo = new CurrentUserInfo(authentication.getName(), Long.parseLong(Objects.requireNonNull(authenticatedPrincipal.getAttribute(OAuth2TokenClaimConstants.CLIENT_ID))));
            ApplicationLinkContext.currentUserInfos.set(currentUserInfo);
        }

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
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) {
        ApplicationLinkContext.clearContext();
    }

}