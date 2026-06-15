package com.maozi.oauth.token.api.rpc;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.param.ClientUserParam;

import java.util.List;
import java.util.Map;

/**
 * OAuth 令牌 RPC 服务接口
 * <p>
 * 定义 OAuth 令牌相关的 RPC 远程调用方法。
 * </p>
 *
 * @author maozi
 */
public interface RpcOauthTokenService {

    /**
     * RPC方式内省令牌
     * <p>
     * 通过Dubbo RPC调用OAuth授权服务器进行令牌内省，
     * 替代HTTP调用introspection端点的方式，减少网络开销。
     * </p>
     *
     * @param token 待内省的令牌字符串
     * @return 令牌内省结果，包含 active、sub、authorities 等字段，
     *         当令牌无效时 active 为 false
     */
    AbstractBaseResult<Map<String, Object>> rpcIntrospect(String token);

    /**
     * RPC方式注销用户令牌
     * <p>
     * 根据客户端ID和用户主体名称，移除该用户在该客户端下的所有OAuth2授权记录，
     * 实现用户注销功能。删除用户或强制下线时调用此方法。
     * </p>
     *
     * @param registeredClientId 已注册客户端ID（对应UserDo.clientId的字符串形式）
     * @param principalName      用户主体名称（用户名）
     * @return 操作结果
     */
    AbstractBaseResult<Void> rpcDestroyByPrincipal(String registeredClientId, String principalName);

    /**
     * RPC方式批量注销用户令牌
     * <p>
     * 根据多个客户端ID和用户主体名称，批量移除对应用户的所有OAuth2授权记录，
     * 减少多次单条RPC调用的网络开销。禁用角色等场景下需要对多个用户统一注销时使用。
     * </p>
     *
     * @param clientUsers 客户端用户参数列表，每项包含clientId和username
     * @return 操作结果
     */
    AbstractBaseResult<Void> rpcDestroyByPrincipals(List<ClientUserParam> clientUsers);

}
