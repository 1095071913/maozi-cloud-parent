package com.maozi.feign.config;

import com.maozi.common.WebUtil;
import com.maozi.common.constant.AuthroizationConstant;
import com.maozi.common.context.ApplicationLinkContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

/**
 * Feign Token 请求拦截器
 * <p>
 * 在 Feign 远程调用时，自动将当前 HTTP 请求中的 Authorization 令牌
 * 和版本号信息传递到下游服务，实现认证和灰度路由的链路传递。
 * </p>
 *
 * @author maozi
 */
@Component
public class TokenRequestInterceptor implements RequestInterceptor {

    /**
     * 在请求模板中添加认证和版本头信息
     *
     * @param requestTemplate Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        HttpServletRequest request = WebUtil.getRequest();

        Enumeration<String> headerNames = request != null ? request.getHeaderNames() : null;
        if (headerNames != null) {

            while (headerNames.hasMoreElements()) {

                String name = headerNames.nextElement();

                if(AuthroizationConstant.AUTHORIZATION_HEADER.equals(name)) {
                    requestTemplate.header(name, request.getHeader(name));
                }

            }

            requestTemplate.header(ApplicationLinkContext.VERSION_KEY, ApplicationLinkContext.versions.get());

        }

    }

}
