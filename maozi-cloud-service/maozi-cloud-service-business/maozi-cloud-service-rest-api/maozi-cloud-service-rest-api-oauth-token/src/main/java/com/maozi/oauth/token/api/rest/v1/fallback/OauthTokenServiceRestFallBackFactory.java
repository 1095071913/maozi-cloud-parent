package com.maozi.oauth.token.api.rest.v1.fallback;

import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.oauth.token.api.rest.v1.RestOauthTokenServiceV1;
import com.maozi.oauth.token.dto.platform.dto.OauthToken;
import com.maozi.oauth.token.dto.platform.param.ClientParam;
import com.maozi.oauth.token.dto.platform.param.TokenInfoParam;
import com.maozi.utils.MapperUtils;
import java.util.Map;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class OauthTokenServiceRestFallBackFactory extends BaseCommon implements FallbackFactory<RestOauthTokenServiceV1>{

	@Override
	public RestOauthTokenServiceV1 create(Throwable e) {

		ErrorResult errorResult = MapperUtils.jsonToPojo(e.getLocalizedMessage(), ErrorResult.class);

		ErrorResult result = BaseCommon.isNull(errorResult) ? ((BusinessResultException)e).getErrorResult() : errorResult;

		return new RestOauthTokenServiceV1() {

			@Override
			public AbstractBaseResult<OauthToken> restGet(TokenInfoParam param) {
				return result;
			}

			@Override
			public AbstractBaseResult<Map> restCheck(String token) {
				return result;
			}

			@Override
			public AbstractBaseResult<OauthToken> restRefresh(String refreshToken,ClientParam param) {
				return result;
			}

			@Override
			public AbstractBaseResult<Void> restDestroy(String token) {
				return result;
			}
		
		};
	}

}
