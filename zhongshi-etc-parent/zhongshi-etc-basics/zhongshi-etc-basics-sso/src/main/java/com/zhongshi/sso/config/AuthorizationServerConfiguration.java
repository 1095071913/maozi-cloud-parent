
/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.zhongshi.sso.config;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import com.zhongshi.sso.config.filter.BootBasicAuthenticationFilter;

/**
 * 
 * 功能说明：Oauth2认证配置
 * 
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 创建日期：2019-08-03 ：1:32:00
 *
 * 版权归属：蓝河团队
 *
 * 协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Resource
	private DataSource dataSource;

	@Resource
	private UserDetailsService userDetailsService;

	@Resource
	private BootBasicAuthenticationFilter filter;

	@Autowired 
	private ClientDetailsServiceConfig clientDetailsService;

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	/**
	 * 注入用于支持 password 模式
	 */
	@Resource
	private AuthenticationManager authenticationManager;

	@Bean 
	public TokenStore tokenStore() {
		//return new JdbcTokenStore(dataSource); 
		return new RedisTokenStore(redisConnectionFactory);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// 用于支持密码模式
				.authenticationManager(authenticationManager).tokenStore(tokenStore())// 不配置会导致token无法刷新
				.userDetailsService(userDetailsService)
				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		filter.setClientDetailsService(clientDetailsService);
		security
				// 允许客户端访问 /oauth/check_token 检查 token // 允许表单登录
				.checkTokenAccess("isAuthenticated()").allowFormAuthenticationForClients()
				.addTokenEndpointAuthenticationFilter(filter);
		;
	}

	/**
	 * 配置客户端
	 *
	 * @param clients
	 * @throws Exception
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

}