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
// 已废弃（@deprecated）：本文件整体以 // 注释停用。
// 替代实现：
//   - 令牌内省：com.maozi.oauth.token.api.OauthTokenService#introspect(String)
//     （通过 Dubbo RPC 调用，替代原 Feign HTTP 方案，减少网络开销）
// 保留此文件仅作为历史参考，请勿取消注释启用。
//--------------------------------------------------------------------------------------------------

/**
 * OAuth 令牌 REST 服务接口
 * <p>
 * 通过 Feign 客户端调用 OAuth 认证服务的令牌管理接口，
 * 支持令牌校验和销毁操作。
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
