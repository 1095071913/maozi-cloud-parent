package com.maozi.oauth.token.api;

import java.util.Map;

/**
 * OAuth 令牌服务接口
 * <p>
 * 定义访问令牌（Access Token）的内省能力，供资源服务器校验令牌有效性。
 * 实际实现由 {@code OauthTokenServiceImpl} 提供，并由 {@code RpcOauthTokenServiceImpl}
 * 通过 Dubbo RPC 对外暴露，资源服务器经 Dubbo 调用完成令牌校验，
 * 替代 HTTP 调用 introspection 端点的方式以减少网络开销。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/24 23:55
 */
public interface OauthTokenService {

    /**
     * 内省访问令牌
     * <p>
     * 通过 Dubbo RPC 调用 OAuth 授权服务器进行令牌内省，
     * 替代 HTTP 调用 introspection 端点的方式，减少网络开销。
     * </p>
     *
     * @param token 待内省的令牌字符串
     * @return 令牌内省结果，包含 active、sub、authorities 等字段，
     *         当令牌无效时 active 为 false
     */
    Map<String, Object> introspect(String token);

}
