package com.maozi.oauth.token.api;

import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/6/24 23:55
 */
public interface OauthTokenService {

    /**
     * 本地方式内省令牌
     * <p>
     * 通过Dubbo RPC调用OAuth授权服务器进行令牌内省，
     * 替代HTTP调用introspection端点的方式，减少网络开销。
     * </p>
     *
     * @param token 待内省的令牌字符串
     * @return 令牌内省结果，包含 active、sub、authorities 等字段，
     *         当令牌无效时 active 为 false
     */
    Map<String, Object> introspect(String token);

}
