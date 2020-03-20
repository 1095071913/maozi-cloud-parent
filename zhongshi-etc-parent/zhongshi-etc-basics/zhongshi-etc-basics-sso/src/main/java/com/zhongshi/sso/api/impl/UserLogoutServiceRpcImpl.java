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

package com.zhongshi.sso.api.impl;

import javax.annotation.Resource;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.sso.api.UserLogoutServiceRpc;

/**
 * 
 * 	功能说明：用户退出
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-04 ：4:37:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class UserLogoutServiceRpcImpl extends BaseResultFactory implements UserLogoutServiceRpc {

	@Resource
	public TokenStore tokenStore;

	@Override
	public AbstractBaseResult userLogout(String token) {
		OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
		tokenStore.removeAccessToken(oAuth2AccessToken);
		return success("退出成功");
	}

	@Override
	@SentinelResource(value = "test", fallback = "dubboFallback", fallbackClass = BaseResultFactory.class)
	public AbstractBaseResult test() throws Exception {
		if(true) {
			throw new Exception("测试成功");
		}
		return success("成功");
	}
	
	@Override
	@SentinelResource(value = "protected-resource", blockHandler = "dubboFallback")
	public AbstractBaseResult test1() throws Exception {
		if(true) {
			throw new Exception("测试成功");
		}
		return success("成功");
	}
	
	
	public AbstractBaseResult dubboFallback(BlockException e) {
		StackTraceElement stackTraceElement = e.getStackTrace()[0];
		System.err.println("============  服务熔断策略     Begin    ==============");
		System.err.println("                                      ");
		System.err.println("服务异常地址："+stackTraceElement.toString());
		System.err.println("                                      ");
		System.err.println("服务异常信息："+e.getLocalizedMessage());
		System.err.println("                                      ");
		System.err.println("============  服务熔断策略     End    ==============");
		return error(code(600));
	}


}
