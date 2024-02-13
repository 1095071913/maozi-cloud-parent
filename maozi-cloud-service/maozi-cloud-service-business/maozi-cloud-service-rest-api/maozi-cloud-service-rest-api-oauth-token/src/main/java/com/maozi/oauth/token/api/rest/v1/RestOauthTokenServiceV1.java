package com.maozi.oauth.token.api.rest.v1;

import com.maozi.base.annotation.Get;
import com.maozi.base.annotation.Post;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.api.rest.v1.fallback.OauthTokenServiceRestFallBackFactory;
import com.maozi.oauth.token.dto.platform.dto.OauthToken;
import com.maozi.oauth.token.dto.platform.param.ClientParam;
import com.maozi.oauth.token.dto.platform.param.TokenInfoParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "【三方】授权令牌")
@FeignClient(value = "maozi-cloud-oauth",fallbackFactory = OauthTokenServiceRestFallBackFactory.class)
public interface RestOauthTokenServiceV1 {

	String PATH = "/oauth/token";

	@Post(value = PATH + "/get",description = "获取令牌")
	AbstractBaseResult<OauthToken> restGet(@RequestBody @Valid TokenInfoParam param) throws Exception;

	@Post(value = PATH + "/{token}/refresh",description = "刷新令牌")
	AbstractBaseResult<OauthToken> restRefresh(@PathVariable("token") String refreshToken,@RequestBody @Valid ClientParam param) throws Exception;

	@Get(value = PATH + "/{token}/check",description = "检查令牌")
	AbstractBaseResult<Map> restCheck(@PathVariable("token") String token);

	@Get(value = PATH + "/{token}/destroy",description = "删除令牌")
	AbstractBaseResult<Void> restDestroy(@PathVariable("token") String token);
	
}
