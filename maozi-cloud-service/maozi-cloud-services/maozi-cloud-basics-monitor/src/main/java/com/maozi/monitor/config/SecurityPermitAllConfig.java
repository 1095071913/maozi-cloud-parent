package com.maozi.monitor.config;

/**
 * 安全配置（已禁用）—— 允许所有请求通过。
 * <p>
 * 此配置类原用于配置 Spring Security，允许所有请求无需认证即可访问监控仪表盘，
 * 并禁用 CSRF 保护。当前已整体注释掉，未启用任何安全策略。
 * 如需开放无认证访问，可取消注释启用此配置。
 * </p>
 */
//package com.maozi.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//public class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().anyRequest().permitAll()
//            .and().csrf().disable();
//    }
//}
