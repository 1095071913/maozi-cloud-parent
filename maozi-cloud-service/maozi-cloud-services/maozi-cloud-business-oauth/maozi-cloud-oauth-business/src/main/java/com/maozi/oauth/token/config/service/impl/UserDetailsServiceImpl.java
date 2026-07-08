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

package com.maozi.oauth.token.config.service.impl;

import com.maozi.common.CollectionUtil;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.service.api.annotation.RemoteResource;
import com.maozi.system.user.api.rpc.RpcUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户详情服务实现类
 * <p>
 * 实现Spring Security的UserDetailsService接口，通过Dubbo RPC远程调用用户服务
 * 获取用户的权限列表和密码信息，构建并返回Spring Security的UserDetails对象。
 * </p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/** 用户服务RPC接口，通过Dubbo远程调用获取用户信息 */
	@RemoteResource
	private RpcUserService rpcUserService;

	/**
	 * 根据用户名加载用户详情
	 * <p>
	 * 通过RPC远程调用获取用户的权限列表和加密密码，
	 * 构建包含用户名、密码和权限列表的UserDetails对象。
	 * 如果用户不存在或RPC调用失败，会根据异常类型返回对应的错误信息。
	 * </p>
	 *
	 * @param username 用户名
	 * @return 包含用户名、密码和权限的UserDetails对象
	 * @throws UsernameNotFoundException 当用户不存在时抛出
	 * @throws BusinessResultException   当业务异常或系统异常时抛出
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		try{

			List<String> permissions = rpcUserService.rpcGetPermissionsByUsername(username).getResultDataThrowError();

			List<GrantedAuthority> grantedAuthorities = CollectionUtil.newArrayList();

			permissions.forEach((permission)->{
				grantedAuthorities.add(new SimpleGrantedAuthority(permission));
			});

			return new User(username,rpcUserService.rpcGetPasswordByUsername(username).getResultDataThrowError(), grantedAuthorities);

		}catch (Exception e){

			if(e instanceof BusinessResultException be && be.getErrorResult().isBusinessError()){
				throw new BusinessResultException(SystemErrorCode.USER_AUTH_ERROR);
			}

			throw new BusinessResultException(SystemErrorCode.SYSTEM_ERROR);

		}

	}

}
