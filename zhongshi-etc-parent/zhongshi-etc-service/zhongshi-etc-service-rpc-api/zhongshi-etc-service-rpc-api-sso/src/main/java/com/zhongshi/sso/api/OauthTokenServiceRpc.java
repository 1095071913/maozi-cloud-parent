package com.zhongshi.sso.api;

import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import com.zhongshi.factory.result.AbstractBaseResult;

public interface OauthTokenServiceRpc {
 
	AbstractBaseResult<Map> checkToken(String token);
	
	AbstractBaseResult<DefaultOAuth2AccessToken> getToken(Map<String, String> parameters);
	
	AbstractBaseResult getToken(Map<String, String> parameters,Map<String, Object> userInfos);
	
	AbstractBaseResult destroyToken(String token);
	
}
