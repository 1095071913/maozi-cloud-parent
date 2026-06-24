package com.maozi.oauth.token.api.impl;

import com.maozi.common.CollectionUtil;
import com.maozi.oauth.constants.OAuth2TokenClaimConstants;
import com.maozi.oauth.token.api.OauthTokenService;
import com.maozi.oauth.token.config.service.OAuth2AuthorizationService;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/6/24 23:58
 */
@Service("oauthTokenService")
public class OauthTokenServiceImpl implements OauthTokenService {

    /** OAuth2授权信息服务，用于管理授权记录的存储、查找和删除 */
    @Resource
    protected OAuth2AuthorizationService authorizationService;

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

        // 构建内省响应，填充令牌基本声明字段
        Map<String, Object> claims = new HashMap<>();
        claims.put(OAuth2TokenClaimConstants.ACTIVE, true);
        claims.put(OAuth2TokenClaimConstants.SUB, authorization.getPrincipalName());
        claims.put(OAuth2TokenClaimConstants.CLIENT_ID, authorization.getRegisteredClientId());

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
