package com.jiumao.sso.api;

import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import com.jiumao.factory.result.AbstractBaseResult;

public interface OauthTokenServiceRpcV1 {
 
	AbstractBaseResult<Map> checkToken(String token);
	
	AbstractBaseResult<DefaultOAuth2AccessToken> getToken(Map<String, String> parameters);
	
	AbstractBaseResult<DefaultOAuth2AccessToken> getToken(Map<String, String> parameters,Map<String, Object> userInfos);
	
	AbstractBaseResult<Void> destroyToken(String token);
	
}
