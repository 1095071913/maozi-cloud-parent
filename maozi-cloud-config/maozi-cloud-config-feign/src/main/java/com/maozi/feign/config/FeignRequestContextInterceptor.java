package com.maozi.feign.config;

import com.maozi.common.JacksonUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.dto.CurrentUserInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

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
public class FeignRequestContextInterceptor implements RequestInterceptor {

    /**
     * 在请求模板中添加认证和版本头信息
     * <p>
     * 当前请求命中白名单路径时，跳过认证和版本头信息的传递。
     * </p>
     *
     * @param requestTemplate Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        CurrentUserInfo currentUserInfo = ApplicationLinkContext.currentUserInfos.get();
        if(ObjectUtil.isNotNullEmpty(currentUserInfo)){
            requestTemplate.header(ApplicationLinkContext.CURRENT_USER_INFO_KEY, JacksonUtil.objectToJson(currentUserInfo));
        }

        requestTemplate.header(ApplicationLinkContext.VERSION_KEY, ApplicationLinkContext.versions.get());
        requestTemplate.header(ApplicationLinkContext.TRACE_ID_KEY, ApplicationLinkContext.traceIds.get());

    }

}
