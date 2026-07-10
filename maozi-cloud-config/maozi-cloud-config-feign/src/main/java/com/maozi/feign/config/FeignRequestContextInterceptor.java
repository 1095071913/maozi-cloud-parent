package com.maozi.feign.config;

import com.maozi.common.JacksonUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.dto.CurrentUserInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign 链路上下文请求拦截器
 * <p>
 * 在 Feign 远程调用时，自动将当前请求的链路上下文（当前登录用户信息、版本号、
 * 链路追踪 ID）透传到下游服务，支撑下游的用户身份识别、灰度路由与日志关联。
 * </p>
 *
 * @author maozi
 */
@Component
public class FeignRequestContextInterceptor implements RequestInterceptor {

    /**
     * 在请求模板中添加链路上下文头信息
     * <p>
     * 当前登录用户信息非空时才写入 {@code X-CurrentUserInfo} 头；
     * {@code X-Version} 和 {@code X-TraceId} 头始终写入（即便值为 null 也会被设置）。
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
