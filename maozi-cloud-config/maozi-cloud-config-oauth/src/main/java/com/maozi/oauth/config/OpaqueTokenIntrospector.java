package com.maozi.oauth.config;

import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.oauth.constants.OAuth2TokenClaimConstants;
import com.maozi.oauth.token.api.rpc.RpcOauthTokenService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 不透明令牌内省器
 * <p>
 * 支持 HTTP 和 Dubbo RPC 两种令牌内省模式，通过配置 {@code maozi.oauth.introspection.mode} 切换：
 * <ul>
 *   <li>{@code http}（默认）：通过 HTTP 调用 OAuth 授权服务器的 introspection 端点</li>
 *   <li>{@code dubbo}：通过 Dubbo RPC 直接调用 OAuth 授权服务器的内省服务，减少网络开销</li>
 * </ul>
 * 内省完成后，从响应中提取 authorities 字段并转换为 {@link GrantedAuthority} 集合，
 * 使权限信息可用于后续的访问控制决策。
 * </p>
 *
 * @author maozi
 */
@Component
public class OpaqueTokenIntrospector implements org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector {

    private static final String MODE_RPC = "rpc";

    /** 令牌内省模式：http 或 rpc */
    private final String mode;

    /** HTTP模式下的内省委托器 */
    private final SpringOpaqueTokenIntrospector httpDelegate;

    /** Dubbo RPC令牌服务引用（check=false避免非Dubbo模式下启动校验失败） */
    @DubboReference(check = false)
    private RpcOauthTokenService rpcOauthTokenService;

    /**
     * 构造方法
     *
     * @param mode            内省模式（http/dubbo），默认 http
     * @param introspectionUri 令牌内省端点 URL（HTTP模式必须配置）
     * @param clientId        OAuth2 客户端 ID（HTTP模式必须配置）
     * @param clientSecret    OAuth2 客户端密钥（HTTP模式必须配置）
     */
    public OpaqueTokenIntrospector(
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.mode:http}") String mode,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri:}") String introspectionUri,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id:}") String clientId,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret:}") String clientSecret) {
        this.mode = mode;
        // 如果是RPC模式则不需要HTTP内省委托器，否则创建Spring默认的HTTP内省器
        this.httpDelegate = MODE_RPC.equalsIgnoreCase(mode) ? null : new SpringOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);

    }

    /**
     * 内省令牌并提取权限信息
     * <p>
     * 根据配置的内省模式（HTTP/Dubbo），调用对应的内省服务完成令牌验证，
     * 然后从响应中提取 authorities 并构建带权限的认证主体。
     * </p>
     *
     * @param token 待内省的令牌字符串
     * @return 包含权限信息的认证主体
     * @throws BadOpaqueTokenException       令牌无效
     * @throws OAuth2IntrospectionException  内省过程中的其他异常
     */
    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            // 根据配置模式选择不同的内省方式：RPC模式走Dubbo调用，否则走HTTP调用
            return MODE_RPC.equalsIgnoreCase(mode) ? dubboIntrospect(token) : httpIntrospect(token);
        } catch (BadOpaqueTokenException e) {
            // 令牌无效异常直接抛出，由上层框架处理（返回401）
            throw e;
        } catch (OAuth2IntrospectionException e) {
            // OAuth2内省过程中的其他异常，转换为令牌无效异常并包装为系统错误
            throw new BadOpaqueTokenException(e.getMessage(), new BusinessResultException(SystemErrorCode.SYSTEM_ERROR).setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE));
        }catch (BusinessResultException e){
            // 业务异常（如RPC调用返回的业务错误），转换为令牌无效异常并保留原始业务错误信息
            throw new BadOpaqueTokenException(e.getMessage(), new BusinessResultException(e.getErrorResult()));
        }
    }

    /**
     * HTTP模式内省令牌
     * <p>
     * 委托给Spring默认的HTTP内省器进行令牌验证，
     * 然后从响应中提取权限信息，补充到认证主体中。
     * </p>
     *
     * @param token 待验证的令牌字符串
     * @return 包含权限信息的认证主体
     */
    private OAuth2AuthenticatedPrincipal httpIntrospect(String token) {
        // 调用Spring默认的HTTP内省器向授权服务器验证令牌
        OAuth2AuthenticatedPrincipal principal = httpDelegate.introspect(token);
        // 从内省响应中提取authorities字段，构建带权限的认证主体
        return enrichWithAuthorities(principal);
    }

    /**
     * Dubbo RPC模式内省令牌
     * <p>
     * 通过Dubbo RPC直接调用授权服务器的令牌内省服务，
     * 避免HTTP调用的网络开销，适合微服务内部调用场景。
     * </p>
     *
     * @param token 待验证的令牌字符串
     * @return 包含权限信息的认证主体
     */
    private OAuth2AuthenticatedPrincipal dubboIntrospect(String token) {
        // 通过Dubbo RPC调用远程令牌内省服务，获取令牌的声明信息（claims）
        Map<String, Object> claims = rpcOauthTokenService.rpcIntrospect(token).getResultDataThrowError();

        // 检查令牌是否处于活跃状态
        Boolean active = (Boolean) claims.get(OAuth2TokenClaimConstants.ACTIVE);
        if (active == null || !active) {
            throw new BadOpaqueTokenException("Token is not active");
        }

        // 从claims中提取权限信息
        Set<GrantedAuthority> authorities = extractAuthorities(claims);
        // 构建认证主体，包含主体名称（sub字段）、全部声明信息和权限集合
        return new DefaultOAuth2AuthenticatedPrincipal(
                (String) claims.getOrDefault(OAuth2TokenClaimConstants.SUB, "unknown"), claims, authorities);
    }

    /**
     * 从HTTP内省响应的authorities属性中提取权限，构建带权限的认证主体
     * <p>
     * Spring默认的HTTP内省器不会自动将authorities字段转换为GrantedAuthority，
     * 因此需要手动提取并构建包含权限信息的认证主体。
     * </p>
     *
     * @param principal 原始的认证主体（不含权限信息）
     * @return 包含权限信息的认证主体
     */
    private OAuth2AuthenticatedPrincipal enrichWithAuthorities(OAuth2AuthenticatedPrincipal principal) {
        // 从内省响应的属性中获取authorities字段
        Object authoritiesObj = principal.getAttribute(OAuth2TokenClaimConstants.AUTHORITIES);
        // 如果authorities不是List类型，说明没有权限信息，直接返回原始主体
        if (!(authoritiesObj instanceof List<?> list)) {
            return principal;
        }

        // 将字符串形式的权限列表转换为GrantedAuthority集合
        Set<GrantedAuthority> grantedAuthorities = list.stream()
                .map(item -> new SimpleGrantedAuthority(item.toString()))
                .collect(Collectors.toSet());

        // 使用原始主体信息和新提取的权限集合，重新构建认证主体
        return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), principal.getAttributes(), grantedAuthorities);
    }

    /**
     * 从Dubbo RPC返回的claims中提取权限
     * <p>
     * 将claims中的authorities字段（字符串列表）转换为Spring Security的
     * {@link GrantedAuthority} 集合。
     * </p>
     *
     * @param claims 令牌内省返回的声明信息
     * @return 权限集合，如果没有authorities字段则返回空集合
     */
    private Set<GrantedAuthority> extractAuthorities(Map<String, Object> claims) {
        Object authoritiesObj = claims.get(OAuth2TokenClaimConstants.AUTHORITIES);
        // 如果authorities不存在或不是List类型，返回空权限集合
        if (!(authoritiesObj instanceof List<?> list)) {
            return Collections.emptySet();
        }
        // 将每个权限字符串转换为SimpleGrantedAuthority
        return list.stream()
                .map(item -> new SimpleGrantedAuthority(item.toString()))
                .collect(Collectors.toSet());
    }

}
