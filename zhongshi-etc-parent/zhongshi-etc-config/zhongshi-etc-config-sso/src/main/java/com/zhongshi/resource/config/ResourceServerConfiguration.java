package com.zhongshi.resource.config;
///*
// * Copyright 2012-2018 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * 
// */
//
//package com.mountain.resource.config;
//
//import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//
//
///**
// * 
// * 	功能说明：SSO认证配置
// * 
// *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
// *
// *	创建日期：2019-08-03 ：1:32:00
// *
// *	版权归属：蓝河团队
// *
// *	协议说明：Apache2.0（ 文件顶端 ）
// *
// */
//  
//
////@Order(1)
////@EnableOAuth2Sso
////@Configuration
////@EnableResourceServer
////@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
//public class ResourceServerConfiguration extends WebSecurityConfigurerAdapter {
//	
//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//		// 设置默认的加密方式
//		return new BCryptPasswordEncoder();
//	}
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
////    	http.csrf().disable()
////    		.antMatcher("/**")
////            .authorizeRequests()  
////            .antMatchers("/", "/login**")
////            .permitAll()
////            .anyRequest()
////            .authenticated();
//    	
//    	 http
//         .exceptionHandling()  
//         .and()
//         .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//         .and()
//         .authorizeRequests()
//         .antMatchers("/**").hasAuthority("USER");
//    	 
//    	 
//    }
//    
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers
//                (
//                		"/user/userRegister",
//                		"/user/login",
//                		"/actuator",
//                		"/actuator/**",
//                		"/swagger-ui.html", 
//                		"/doc.html",
//                		"/swagger-ui.html/**",
//                		"/webjars/**",
//                        "/swagger-resources/**", 
//                        "/v2/api-docs/**",
//                        "/swagger-resources/configuration/ui/**", 
//                        "/swagger-resources/configuration/security/**"
//                );
//    	
//    	
//    }
//    
//    
//    
//}
//
//
