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

/**
 * OAuth 令牌服务降级工厂
 * <p>
 * 当 OAuth 认证服务不可用时，提供降级响应，将服务端的错误信息原样返回。
 * </p>
 *
 * @author maozi
 */
@Component
public class OauthTokenServiceRestFallBackFactory implements FallbackFactory<RestOauthTokenService>{

    /**
     * 创建降级实例
     *
     * @param e 异常信息
     * @return 降级的 OAuth 令牌服务实例
     */
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
