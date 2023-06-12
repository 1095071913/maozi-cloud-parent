package com.maozi.sso.oauth.api.rest.v1.fallback;

import java.util.Map;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.sso.oauth.api.rest.v1.RestOauthTokenServiceV1;
import com.maozi.sso.oauth.dto.platform.dto.OauthToken;
import com.maozi.sso.oauth.dto.platform.param.ClientParam;
import com.maozi.sso.oauth.dto.platform.param.TokenInfoParam;
import com.maozi.tool.MapperUtils;

@Component
public class OauthTokenServiceRestFallBackFactory extends BaseCommon implements FallbackFactory<RestOauthTokenServiceV1>{

	@Override
	public RestOauthTokenServiceV1 create(Throwable cause) {
		
		return new RestOauthTokenServiceV1() {

			@Override
			public AbstractBaseResult<OauthToken> restGet(TokenInfoParam param) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}

			@Override
			public AbstractBaseResult<Map> restCheck(String token) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}

			@Override
			public AbstractBaseResult<OauthToken> restRefresh(String refreshToken,ClientParam param) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}

			@Override
			public AbstractBaseResult<Void> restDestroy(String token) {
				return MapperUtils.jsonToPojo(cause.getLocalizedMessage(), ErrorResult.class);
			}
		
		};
	}

}
