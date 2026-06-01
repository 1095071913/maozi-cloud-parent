package com.maozi.oauth.config;

import com.maozi.common.CollectionUtil;
import com.maozi.oauth.properties.ApiWhitelistProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class ResourceServerConfig {

    @Resource
    public ApiWhitelistProperties apiWhitelistProperties;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        List<String> witelist = CollectionUtil.newArrayList();
        witelist.addAll(ApiWhitelistProperties.DEFAULT_WITE_LIST);
        witelist.addAll(apiWhitelistProperties.getConfigWhitelist());

        String[] requestMatchers = witelist.toArray(new String[0]);
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(requestMatchers).permitAll()
                .anyRequest().authenticated());

        // 添加BearerTokenAuthenticationFilter，将认证服务当做一个资源服务，解析请求头中的token
        http.oauth2ResourceServer((resourceServer) -> resourceServer
                .opaqueToken(Customizer.withDefaults())
                .accessDeniedHandler(new IAccessDeniedHandler())
                .authenticationEntryPoint(new IAuthenticationEntryPoint())
        );

        return http.build();

    }

//    /**
//     * 根据jwtDecoder和令牌自省生成{@link AuthenticationManagerResolver }，在AuthenticationManagerResolver中根据当前请求决定使用jwt解析器还是去token自省端点获取当前token信息
//     *
//     * @param jwtDecoder              jwt解析器
//     * @param opaqueTokenIntrospector token自省
//     * @return 返回 {@link AuthenticationManagerResolver }
//     */
//    @Bean
//    AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver
//    (JwtDecoder jwtDecoder, OpaqueTokenIntrospector opaqueTokenIntrospector) {
//        AuthenticationManager jwt = new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
//        AuthenticationManager opaqueToken = new ProviderManager(
//                new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector));
//        return (request) -> useJwt(request) ? jwt : opaqueToken;
//    }
//
//    /**
//     * 判断请求头是否有key ： token-type，有值不是jwt
//     * 这里根据自己业务实现，可以获取token后再判断token是jwt还是匿名token
//     *
//     * @param request 请求对象
//     * @return 是否使用jwt token
//     */
//    private boolean useJwt(HttpServletRequest request) {
//        return ObjectUtils.isEmpty(request.getHeader("token-type"));
//    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver(OpaqueTokenIntrospector opaqueTokenIntrospector) {
        AuthenticationManager opaqueToken = new ProviderManager(new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector));
        return (request) -> opaqueToken;
    }




}