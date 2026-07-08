package com.maozi.oauth.token.api.impl;

import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.oauth.token.api.OauthTokenService;
import com.maozi.oauth.token.config.service.OAuth2AuthorizationService;
import com.maozi.oauth.token.constants.OAuth2TokenClaimConstants;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth 令牌服务本地实现
 * <p>
 * 实现资源服务器对访问令牌（Access Token）的内省逻辑：基于
 * {@link OAuth2AuthorizationService#findByToken} 查询授权记录，
 * 校验令牌是否仍处于有效状态，并按需返回 active、sub、client_id、authorities、scope 等声明。
 * 该本地实现由 {@code RpcOauthTokenServiceImpl} 通过 Dubbo RPC 对外暴露，
 * 资源服务器经 Dubbo 调用即可完成令牌校验。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/24 23:58
 */
@Service("oauthTokenService")
public class OauthTokenServiceImpl implements OauthTokenService {

    /** OAuth2授权信息服务，用于管理授权记录的存储、查找和删除 */
    @Resource
    protected OAuth2AuthorizationService authorizationService;

    @Resource
    protected RegisteredClientRepository registeredClientRepository;

    /**
     * {@inheritDoc}
     * <p>
     * 实现要点：
     * <ul>
     *   <li>先按 access_token 维度查找授权记录，未命中直接返回 active=false；</li>
     *   <li>命中后再校验 AccessToken 是否仍处于 active 状态；</li>
     *   <li>仅提取 Hessian 可序列化的字段，避免复制可能含 URL 等不可序列化类型的全部 claims。</li>
     * </ul>
     * </p>
     */
    @Override
    public Map<String, Object> introspect(String token) {

        // 通过access_token查找对应的授权记录
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) {
            return CollectionUtil.newHashMap(OAuth2TokenClaimConstants.ACTIVE, false);
        }

        // 校验授权记录中的AccessToken是否有效
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getAccessToken();
        if (accessToken == null || !accessToken.isActive()) {
            return CollectionUtil.newHashMap(OAuth2TokenClaimConstants.ACTIVE, false);
        }

        RegisteredClient authorizedClient = this.registeredClientRepository.findById(authorization.getRegisteredClientId());
        if(ObjectUtil.isNullEmpty(authorizedClient)){
            return CollectionUtil.newHashMap(OAuth2TokenClaimConstants.ACTIVE, false);
        }

        // 构建内省响应，填充令牌基本声明字段
        Map<String, Object> claims = new HashMap<>();
        claims.put(OAuth2TokenClaimConstants.ACTIVE, true);
        claims.put(OAuth2TokenClaimConstants.SUB, authorization.getPrincipalName());
        claims.put(OAuth2TokenClaimConstants.CLIENT_ID, authorizedClient.getClientId());
        claims.put(OAuth2TokenClaimConstants.TOKEN_TYPE,accessToken.getToken().getTokenType().getValue());

        // 只提取Hessian可序列化的字段，避免复制可能包含URL等不可序列化类型的全部claims
        if (accessToken.getClaims() != null) {
            Object authorities = accessToken.getClaims().get(OAuth2TokenClaimConstants.AUTHORITIES);
            if (authorities != null) {
                claims.put(OAuth2TokenClaimConstants.AUTHORITIES, authorities);
            }
            Object scope = accessToken.getClaims().get(OAuth2TokenClaimConstants.SCOPE);
            if (scope != null) {
                claims.put(OAuth2TokenClaimConstants.SCOPE, scope);
            }
        }

        return claims;

    }

}
