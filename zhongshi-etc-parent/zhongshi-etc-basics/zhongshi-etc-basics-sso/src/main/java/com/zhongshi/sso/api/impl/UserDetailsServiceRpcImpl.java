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

import com.google.common.collect.Lists;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.success.SuccessResult;
import com.zhongshi.sso.api.UserDetailsServiceRpc;
import com.zhongshi.user.UserDo;
import com.zhongshi.user.rpc.api.UserServiceRpc;

import java.util.List;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * 	功能说明：用户登录SSO RPC 实现
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-03 ：23:08:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class UserDetailsServiceRpcImpl extends BaseResultFactory implements UserDetailsServiceRpc {

    @Reference
    private UserServiceRpc userServiceRpc;

    @Override
    public AbstractBaseResult loadUserByUsername(String username) throws UsernameNotFoundException {
    	UserDo userVO=UserDo.builder().username(username).build();
    	AbstractBaseResult<UserDo> result = userServiceRpc.rpcSelectUserOne(userVO);
        // 查询用户信息
    	if(result.ifStatus()){
    		UserDo user = ((SuccessResult<UserDo>) result).getDatabeans().get(0).getData();
    		if(!isNotNull(user)){
    			List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
    			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    			// 由框架完成认证工作
    	        return success(new User(user.getUsername(), user.getPassword(), grantedAuthorities));
    		}
    	}
    	return result;
//    	List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
//    	grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//    	
//    	
//    	return new User(username,passwordEncoder.encode("123456"),grantedAuthorities);
        
//        if (tbUser != null) {
//            // 获取用户授权
//            List<TbPermission> tbPermissions = tbPermissionService.selectByUserId(tbUser.getId());
//
//            // 声明用户授权
//            tbPermissions.forEach(tbPermission -> {
//                if (tbPermission != null && tbPermission.getEnname() != null) {
//                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(tbPermission.getEnname());
//                    grantedAuthorities.add(grantedAuthority);
//                }
//            });
//        }
        
        
    }
    
}