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

@Component
public class OpaqueTokenIntrospector extends SpringOpaqueTokenIntrospector {

    public OpaqueTokenIntrospector(
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}") String introspectionUri,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}") String clientId,
            @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}") String clientSecret) {
        super(introspectionUri, clientId, clientSecret);
    }

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
