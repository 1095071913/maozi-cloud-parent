package com.maozi.oauth.token.api.rest.fallback;

import com.maozi.common.JacksonUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.oauth.token.api.rest.RestOauthTokenService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OauthTokenServiceRestFallBackFactory implements FallbackFactory<RestOauthTokenService>{

	@Override
	public RestOauthTokenService create(Throwable e) {

		ErrorResult errorResult = JacksonUtil.jsonToObject(e.getLocalizedMessage(), ErrorResult.class);

		ErrorResult result = ObjectUtil.isNullEmpty(errorResult) ? ((BusinessResultException)e).getErrorResult() : errorResult;

		return new RestOauthTokenService() {

			@Override
			public AbstractBaseResult<Map<String,?>> restCheck(String token) {
				return result;
			}

			@Override
			public AbstractBaseResult<Void> restDestroy(String token) {
				return result;
			}
		
		};
	}

}
