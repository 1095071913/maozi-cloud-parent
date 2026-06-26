package com.maozi.service.api.config;

import com.maozi.oauth.api.service.RemoteOauthTokenServiceImpl;
import com.maozi.oauth.token.api.OauthTokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 资源服务器令牌内省服务自动装配。
 * <p>
 * 仅当容器中不存在任何 {@link OauthTokenService} 实现时（即非授权服务器进程），
 * 才注册 {@link RemoteOauthTokenServiceImpl} 作为 Dubbo 远程代理实现。
 * 授权服务器进程已有本地 {@code OauthTokenServiceImpl}，本配置不会生效。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/26
 */
@Configuration
public class RemoteOauthTokenServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean(OauthTokenService.class)
    public OauthTokenService oauthTokenService() {
        return new RemoteOauthTokenServiceImpl();
    }

}
