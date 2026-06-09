package com.maozi.oauth.config;

import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 不透明令牌内省器
 * <p>
 * 扩展 Spring Security 的 {@link SpringOpaqueTokenIntrospector}，在令牌内省完成后，
 * 从内省响应中提取 authorities 字段并转换为 {@link GrantedAuthority} 集合，
 * 使权限信息可用于后续的访问控制决策。
 * </p>
 *
 * @author maozi
 */
@Component
public class OpaqueTokenIntrospector extends SpringOpaqueTokenIntrospector {

    /**
     * 构造方法，配置令牌内省端点信息
     *
     * @param introspectionUri 令牌内省端点 URL
     * @param clientId OAuth2 客户端 ID
     * @param clientSecret OAuth2 客户端密钥
     */
    public OpaqueTokenIntrospector(
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}") String introspectionUri,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}") String clientId,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}") String clientSecret) {
        super(introspectionUri, clientId, clientSecret);
    }

    /**
     * 内省令牌并提取权限信息
     * <p>
     * 调用父类完成令牌验证后，从响应的 authorities 属性中提取权限列表，
     * 转换为 {@link SimpleGrantedAuthority} 集合并构建新的认证主体。
     * </p>
     *
     * @param token 待内省的令牌字符串
     * @return 包含权限信息的认证主体
     * @throws BadOpaqueTokenException 令牌无效
     * @throws OAuth2IntrospectionException 内省过程中的其他异常
     */
    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            OAuth2AuthenticatedPrincipal principal = super.introspect(token);

            // 从 introspection 响应中提取 authorities 字段，转为 GrantedAuthority
            Object authoritiesObj = principal.getAttribute("authorities");
            if (!(authoritiesObj instanceof List<?> list)) {
                return principal;
            }

            Set<GrantedAuthority> grantedAuthorities = list.stream()
                    .map(item -> new SimpleGrantedAuthority(item.toString()))
                    .collect(Collectors.toSet());

            return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), principal.getAttributes(), grantedAuthorities);
        } catch (BadOpaqueTokenException e) {
            throw e;
        } catch (OAuth2IntrospectionException e) {
            throw new BadOpaqueTokenException(e.getMessage(), new BusinessResultException(SystemErrorCode.SYSTEM_ERROR).setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE));
        }
    }

}
