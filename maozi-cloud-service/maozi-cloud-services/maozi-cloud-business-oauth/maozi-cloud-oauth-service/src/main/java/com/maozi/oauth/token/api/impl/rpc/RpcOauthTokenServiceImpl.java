package com.maozi.oauth.token.api.impl.rpc;

import com.maozi.common.CollectionUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.constants.OAuth2TokenClaimConstants;
import com.maozi.oauth.token.api.rpc.RpcOauthTokenService;
import com.maozi.oauth.token.config.service.OAuth2AuthorizationService;
import com.maozi.oauth.token.param.ClientUserParam;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth令牌Dubbo RPC服务实现类
 * <p>
 * 通过Dubbo RPC对外暴露令牌内省能力，资源服务器可直接通过RPC调用
 * 进行令牌校验，替代HTTP调用introspection端点的方式，减少网络开销。
 * </p>
 */
@DubboService
public class RpcOauthTokenServiceImpl implements RpcOauthTokenService {

    /** OAuth2授权信息服务，用于管理授权记录的存储、查找和删除 */
    @Resource
    private OAuth2AuthorizationService authorizationService;

    /**
     * RPC令牌内省接口
     * <p>
     * 根据传入的AccessToken值查找对应的授权记录，验证令牌是否有效，
     * 并返回令牌的声明信息（用户名、客户端ID、权限列表、授权范围等）。
     * 如果令牌无效或已过期，返回active=false。
     * </p>
     *
     * @param token 待内省的访问令牌值
     * @return 包含令牌声明信息的Map，至少包含active字段标识令牌是否有效
     */
    @Override
    public AbstractBaseResult<Map<String, Object>> rpcIntrospect(String token) {

        // 通过access_token查找对应的授权记录
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) {
            return ResultUtil.success(CollectionUtil.newHashMap(OAuth2TokenClaimConstants.ACTIVE, false));
        }

        // 校验授权记录中的AccessToken是否有效
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getAccessToken();
        if (accessToken == null || !accessToken.isActive()) {
            return ResultUtil.success(CollectionUtil.newHashMap(OAuth2TokenClaimConstants.ACTIVE, false));
        }

        // 构建内省响应，填充令牌基本声明字段
        Map<String, Object> claims = new HashMap<>();
        claims.put(OAuth2TokenClaimConstants.ACTIVE, true);
        claims.put(OAuth2TokenClaimConstants.SUB, authorization.getPrincipalName());
        claims.put(OAuth2TokenClaimConstants.CLIENT_ID, authorization.getRegisteredClientId());

        // 只提取Hessian可序列化的字段，避免复制可能包含URL等不可序列化类型的全部claims
        if (accessToken.getClaims() != null) {
            Object authorities = accessToken.getClaims().get(OAuth2TokenClaimConstants.AUTHORITIES);
            if (authorities != null) {
                claims.put(OAuth2TokenClaimConstants.AUTHORITIES, authorities);
            }
            Object scope = accessToken.getClaims().get(OAuth2TokenClaimConstants.SCOPE);
            if (scope != null) {
                claims.put(OAuth2TokenClaimConstants.SCOPE, scope);
            }
        }

        return ResultUtil.success(claims);
    }

    /**
     * RPC根据客户端ID和用户名注销授权
     * <p>
     * 移除指定客户端下指定用户的所有OAuth2授权记录，实现单用户注销功能。
     * </p>
     *
     * @param registeredClientId 已注册客户端ID
     * @param principalName      用户主体名称（用户名）
     * @return 无数据的统一响应结果
     */
    @Override
    public AbstractBaseResult<Void> rpcDestroyByPrincipal(String registeredClientId, String principalName) {
        // 根据clientId+用户名移除该用户在该客户端下的所有OAuth2授权
        authorizationService.removeAllByPrincipal(registeredClientId, principalName);
        return ResultUtil.success();
    }

    /**
     * RPC批量注销多个用户的授权
     * <p>
     * 批量移除多个用户的所有OAuth2授权记录，内部通过MGET+批量DEL减少Redis网络往返，
     * 适用于角色禁用等需要同时注销多个用户的场景。
     * </p>
     *
     * @param clientUsers 客户端用户参数列表，每项包含clientId和username
     * @return 无数据的统一响应结果
     */
    @Override
    public AbstractBaseResult<Void> rpcDestroyByPrincipals(List<ClientUserParam> clientUsers) {
        // 批量移除多个用户的所有OAuth2授权，内部通过MGET+批量DEL减少Redis网络往返
        authorizationService.removeAllByPrincipals(clientUsers);
        return ResultUtil.success();
    }

}
