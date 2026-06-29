package com.maozi.monitor.config;

/**
 * 安全配置（已禁用）—— 基于表单登录的安全认证。
 * <p>
 * 此配置类原用于配置 Spring Boot Admin 的安全认证，包含以下功能：
 * - 静态资源、健康检查、登录页面允许匿名访问；
 * - 其他请求需要认证；
 * - 表单登录（登录页面、成功处理器）；
 * - HTTP Basic 认证；
 * - CSRF 保护（使用 Cookie 存储，并对部分接口忽略）；
 * - Remember-Me 记住我功能（有效期 14 天）；
 * - 内存中的用户认证（使用 Spring Security 配置的用户名密码）。
 * 当前已整体注释掉，未启用安全认证。
 * 如需启用安全认证，可取消注释启用此配置。
 * </p>
 */
//package com.maozi.config;
//
//import java.util.UUID;
//
//import org.springframework.boot.autoconfigure.security.SecurityProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import de.codecentric.boot.admin.server.config.AdminServerProperties;
//
//@Configuration(proxyBeanMethods = false)
//public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
//
//	/** Spring Boot Admin 服务端属性 */
//	private final AdminServerProperties adminServer;
//
//	/** Spring Security 安全属性（包含默认用户名密码） */
//	private final SecurityProperties security;
//
//	public SecuritySecureConfig(AdminServerProperties adminServer, SecurityProperties security) {
//		this.adminServer = adminServer;
//		this.security = security;
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// 登录成功后重定向处理器
//		SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
//		successHandler.setTargetUrlParameter("redirectTo");
//		successHandler.setDefaultTargetUrl(this.adminServer.path("/"));
//
//		http.authorizeRequests((authorizeRequests) -> authorizeRequests.antMatchers(this.adminServer.path("/assets/**"))
//				.permitAll().antMatchers(this.adminServer.path("/actuator/info")).permitAll()
//				.antMatchers(this.adminServer.path("/actuator/health")).permitAll()
//				.antMatchers(this.adminServer.path("/login")).permitAll().anyRequest().authenticated())
//				.formLogin((formLogin) -> formLogin.loginPage(this.adminServer.path("/login"))
//						.successHandler(successHandler).and())
//				.logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout")))
//				.httpBasic(Customizer.withDefaults())
//				.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//						.ignoringRequestMatchers(
//								new AntPathRequestMatcher(this.adminServer.path("/instances"),
//										HttpMethod.POST.toString()),
//								new AntPathRequestMatcher(this.adminServer.path("/instances/*"),
//										HttpMethod.DELETE.toString()),
//								new AntPathRequestMatcher(this.adminServer.path("/actuator/**"))))
//				.rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
//	}
//
//	// Required to provide UserDetailsService for "remember functionality"
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser(security.getUser().getName())
//				.password("{noop}" + security.getUser().getPassword()).roles("USER");
//	}
//
//}
