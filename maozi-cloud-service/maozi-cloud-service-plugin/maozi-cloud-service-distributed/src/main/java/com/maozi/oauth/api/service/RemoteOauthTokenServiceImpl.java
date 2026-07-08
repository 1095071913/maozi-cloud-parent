package com.maozi.oauth.api.service;

import com.maozi.common.ObjectUtil;
import com.maozi.oauth.token.api.OauthTokenService;
import com.maozi.oauth.token.api.rest.RestOauthTokenService;
import com.maozi.oauth.token.api.rpc.RpcOauthTokenService;
import com.maozi.service.api.annotation.RemoteResource;
import com.maozi.service.config.RemoteOauthTokenServiceConfiguration;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 资源服务器进程中的令牌内省服务实现。
 * <p>
 * 通过 Dubbo RPC 远程调用授权服务器的 {@link RpcOauthTokenService} 完成令牌内省，
 * 供 {@link com.maozi.oauth.config.OpaqueTokenIntrospector} 在非授权服务器进程中使用。
 * </p>
 * <p>
 * 本类不通过 {@code @Service} 注册，而是由 {@link RemoteOauthTokenServiceConfiguration}
 * 通过 {@code @Bean @ConditionalOnMissingBean} 按需注册——当容器中已有本地
 * {@link OauthTokenService} 实现时（授权服务器进程），本类不会被创建。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/26 08:12
 */
public class RemoteOauthTokenServiceImpl implements OauthTokenService {

    private static final String MODE_RPC = "rpc";

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.mode:}")
    private String mode;

    @RemoteResource
    private RpcOauthTokenService rpcOauthTokenService;

    @Resource
    private RestOauthTokenService restOauthTokenService;

    @Override
    public Map<String, Object> introspect(String token) {
        return ObjectUtil.isNullEmpty(mode) || MODE_RPC.equalsIgnoreCase(mode) ?
                rpcOauthTokenService.rpcIntrospect(token).getResultDataThrowError()
                :
                restOauthTokenService.restIntrospect(token).getResultDataThrowError();
    }

}
