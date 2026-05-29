package com.maozi.oauth.token.api.rest;

import com.maozi.base.annotation.Get;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.api.rest.fallback.OauthTokenServiceRestFallBackFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Tag(name = "【三方】授权令牌")
@FeignClient(value = "maozi-cloud-oauth",fallbackFactory = OauthTokenServiceRestFallBackFactory.class)
public interface RestOauthTokenService {

	String PATH = "/oauth/token";

	@Get(value = PATH + "/{token}/check",description = "检查令牌")
	AbstractBaseResult<Map<String, ?>> restCheck(@PathVariable("token") String token);

	@Get(value = PATH + "/{token}/destroy",description = "删除令牌")
	AbstractBaseResult<Void> restDestroy(@PathVariable("token") String token);
	
}
