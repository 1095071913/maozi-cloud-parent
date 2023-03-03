package com.maozi.sso.oauth.api.rest.v1;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maozi.factory.result.AbstractBaseResult;
import com.maozi.sso.oauth.api.rest.v1.fallback.OauthTokenServiceRestFallBackFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(tags = "【第三方】Token接口")
@FeignClient(value = "maozi-cloud-sso",fallbackFactory = OauthTokenServiceRestFallBackFactory.class)
public interface OauthTokenServiceRestV1 {

	@GetMapping("/oauth/token/check")
	@ApiOperation(value = "检查Token")
	AbstractBaseResult<Map> checkToken(@RequestParam("token") String token);
	
	@GetMapping("/oauth/token/get")
	@ApiOperation(value = "获取Token")
	AbstractBaseResult<DefaultOAuth2AccessToken> getToken(@RequestParam("parameters") Map<String, String> parameters);
	
	@GetMapping("/oauth/token/destroy")
	@ApiOperation(value = "删除Token")
	AbstractBaseResult<Void> destroyToken(@RequestParam("token") String token);
	
}
