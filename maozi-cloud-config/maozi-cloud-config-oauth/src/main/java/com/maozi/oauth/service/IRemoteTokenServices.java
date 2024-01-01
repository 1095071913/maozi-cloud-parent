package com.maozi.oauth.service;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.oauth.token.api.rest.v1.RestOauthTokenServiceV1;
import com.maozi.oauth.token.api.rpc.v1.RpcOauthTokenServiceV1;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
public class IRemoteTokenServices implements ResourceServerTokenServices {

    @Value("${security.oauth2.resource.type}")
    private String type;

    @Resource
    private RestOauthTokenServiceV1 restOauthTokenService;

    @DubboReference
    private RpcOauthTokenServiceV1 rpcOauthTokenService;

    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {

        AbstractBaseResult<Map> checkTokenResult = "HTTP".equals(type) ? restOauthTokenService.restCheck(accessToken) : rpcOauthTokenService.rpcCheck(accessToken);

        if(!checkTokenResult.isSuccess()) {
            throw new InvalidTokenException(((ErrorResult)checkTokenResult).getMessage());
        }

        Map<String,Object> checkUserResult = checkTokenResult.getData();

        if (checkUserResult.containsKey("error") || !Boolean.TRUE.equals(checkUserResult.get("active"))) {
            throw new InvalidTokenException("Token错误");
        }

        return tokenConverter.extractAuthentication(checkUserResult);

    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

}
