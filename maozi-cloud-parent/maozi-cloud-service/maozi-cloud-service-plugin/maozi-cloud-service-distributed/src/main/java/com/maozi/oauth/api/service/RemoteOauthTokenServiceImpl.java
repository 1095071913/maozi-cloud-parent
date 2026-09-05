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
 * 根据配置模式远程调用授权服务器完成令牌内省：模式为空或 rpc 时通过 Dubbo RPC 调用
 * {@link RpcOauthTokenService}，其余模式通过 REST（Feign）调用 {@link RestOauthTokenService}，
 * 供 {@code OpaqueTokenIntrospector} 使用。本类由 {@link RemoteOauthTokenServiceConfiguration}
 * 在容器中不存在本地 {@link OauthTokenService} 实现时按需注册（授权服务器进程不创建本类）。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/26 08:12
 */
public class RemoteOauthTokenServiceImpl implements OauthTokenService {

    /** 内省调用模式：RPC（Dubbo） */
    private static final String MODE_RPC = "rpc";

    /** 内省调用模式，rpc 走 Dubbo，其余走 REST，默认 rpc */
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.mode:}")
    private String mode;

    /** Dubbo 远程令牌服务引用 */
    @RemoteResource
    private RpcOauthTokenService rpcOauthTokenService;

    /** REST 远程令牌服务引用（Feign） */
    @Resource
    private RestOauthTokenService restOauthTokenService;

    /**
     * 远程内省令牌
     * <p>
     * 模式为空或 {@code rpc} 时走 Dubbo RPC，否则走 REST（Feign）调用；
     * 内省失败（结果为错误）时直接抛出业务异常。
     * </p>
     *
     * @param token 访问令牌
     * @return 令牌内省声明信息
     */
    @Override
    public Map<String, Object> introspect(String token) {
        return ObjectUtil.isNullEmpty(mode) || MODE_RPC.equalsIgnoreCase(mode) ?
                rpcOauthTokenService.rpcIntrospect(token).getResultDataThrowError()
                :
                restOauthTokenService.restIntrospect(token).getResultDataThrowError();
    }

}
