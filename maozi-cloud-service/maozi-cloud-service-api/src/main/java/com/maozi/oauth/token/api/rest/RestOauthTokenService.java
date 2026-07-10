package com.maozi.oauth.token.api.rest;

import com.maozi.common.constant.ApplicationNameConstant;
import com.maozi.common.constant.RemoteConstant;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.api.rest.fallback.RestOauthTokenServiceFallBackFactory;
import com.maozi.service.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

//--------------------------------------------------------------------------------------------------
// 兼容说明：本接口仍以 Feign HTTP 形式保留为兼容入口，代码处于生效状态。
// 主链路已切换为 Dubbo RPC 实现，详见：
//   - com.maozi.oauth.token.api.OauthTokenService#introspect(String)
//   - com.maozi.oauth.token.api.impl.rpc.RpcOauthTokenServiceImpl
// 新业务请直接使用上述 Dubbo 接口，避免引入新的 Feign 调用。
//--------------------------------------------------------------------------------------------------

/**
 * OAuth 令牌 REST 服务接口
 * <p>
 * 通过 Feign 客户端调用 OAuth 认证服务的令牌管理接口，
 * 支持令牌校验和销毁操作。
 * </p>
 * <p>
 * <b>当前状态：</b>代码处于生效状态，仍可作为 Feign 兼容入口使用；
 * 但令牌内省的主链路已切换为 Dubbo RPC（{@link com.maozi.oauth.token.api.OauthTokenService}）。
 * </p>
 *
 * @author maozi
 */
@Tag(name = "【三方】授权令牌")
@FeignClient(value = ApplicationNameConstant.MAOZI_CLOUD_OAUTH_SERVICE, fallbackFactory = RestOauthTokenServiceFallBackFactory.class)
public interface RestOauthTokenService {

    /** 基础路径 */
	String PATH = RemoteConstant.REMOTE_PREFIX_PATH + "/oauth/token";

    /**
     * 内省令牌
     *
     * @param token 令牌字符串
     * @return 令牌信息
     */
	@Get(value = PATH + "/{token}/introspect",description = "内省令牌")
	AbstractBaseResult<Map<String, Object>> restIntrospect(@PathVariable("token") String token);

}
