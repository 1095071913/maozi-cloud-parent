
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

package com.mountain.user.api.impl;

import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.mountain.api.base.service.impl.BaseServiceImpl;
import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.factory.result.code.CodeAttribute;
import com.mountain.factory.result.code.CodeHashMap;
import com.mountain.user.mapper.UserMapper;
import com.mountain.sso.api.UserDetailsServiceRpc;
import com.mountain.sso.api.UserLogoutServiceRpc;
import com.mountain.user.UserDo;
import com.mountain.user.api.UserService;

/**
 * 
 * 	功能说明：用户服务实现
 * 
 * 	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 	创建日期：2019-08-03 ：1:32:00
 *
 * 	版权归属：蓝河团队
 *
 * 	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class UserServiceImpl extends BaseServiceImpl<UserDo, UserMapper> implements UserService {

	static {

		code(new CodeHashMap(applicationName, port) {
			
			{

				this.put(new CodeAttribute(40001, "图形认证码不正确"));

				this.put(new CodeAttribute(40002, "用户已存在"));

				this.put(new CodeAttribute(40003, "账号或密码错误"));

				this.put(new CodeAttribute(40004, "用户添加失败"));

				this.put(new CodeAttribute(40005, "用户密码错误"));

				this.put(new CodeAttribute(40006, "用户旧密码或新密码不能为空"));
				
				this.put(new CodeAttribute(40007, "用户未登录"));

			}
			
		});

	}

	
	
	@Reference
	protected UserDetailsServiceRpc userDetailsServicePRC;

	@Reference
	protected UserLogoutServiceRpc userLogoutService;

	@Resource
	protected BCryptPasswordEncoder passwordEncoder;

	

	
	//查询用户信息
	protected AbstractBaseResult<UserDo> selectUserOne(UserDo userDo) {
		try {
			return success(this.selectOne(UserDo.builder().username(userDo.getUsername()).build()).get());
		} catch (InterruptedException | ExecutionException e) {
			return error(code(40007));
		}
	}
	
	
	
}
