package com.maozi.sso.oauth.api.rest.v1.fallback;

import java.util.Map;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.stereotype.Component;

import com.maozi.factory.BaseResultFactory;
import com.maozi.factory.result.AbstractBaseResult;
import com.maozi.factory.result.error.ErrorResult;
import com.maozi.sso.oauth.api.rest.v1.OauthTokenServiceRestV1;
import com.maozi.tool.MapperUtils;

@Component
public class OauthTokenServiceRestFallBackFactory extends BaseResultFactory implements FallbackFactory<OauthTokenServiceRestV1>{

	@Override
	public OauthTokenServiceRestV1 create(Throwable cause) {
		
		return new OauthTokenServiceRestV1() {

			@Override
			public AbstractBaseResult<Map> checkToken(String token) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}

			@Override
			public AbstractBaseResult<DefaultOAuth2AccessToken> getToken(Map<String, String> parameters) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}

			@Override
			public AbstractBaseResult<Void> destroyToken(String token) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}
		
		};
	}

}
