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

package com.maozi.oauth.config;

import com.maozi.oauth.properties.ApiWhitelistProperties;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * 
 * 	功能说明：SSO认证配置
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-25 : 17:59:00
 *
 *	版权归属：蓝河团队
 *   
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Resource
	public ApiWhitelistProperties apiWhitelistProperties;

	@Resource
	public ResourceServerTokenServices remoteTokenServices;

	@Override
	public void configure(ResourceServerSecurityConfigurer resource) throws Exception {

		resource.resourceId("backend-resources");
		
		resource.accessDeniedHandler(new IAccessDeniedHandler());
		
		resource.authenticationEntryPoint(new IAuthenticationEntryPoint());

		resource.tokenServices(remoteTokenServices);
		
	}

    @Override
    public void configure(HttpSecurity http) throws Exception {
    	
    	 apiWhitelistProperties.getDefaultWitelist().addAll(apiWhitelistProperties.getWhitelist());
    	
    	 http.exceptionHandling().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    	 
    	 .and().authorizeRequests().antMatchers(apiWhitelistProperties.getDefaultWitelist().toArray(new String[apiWhitelistProperties.getDefaultWitelist().size()])).permitAll()
    	 
    	 .and().authorizeRequests().anyRequest().authenticated();
    	
    }
    

}