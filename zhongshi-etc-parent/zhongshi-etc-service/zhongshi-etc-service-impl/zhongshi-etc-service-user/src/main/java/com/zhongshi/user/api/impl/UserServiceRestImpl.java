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

package com.zhongshi.user.api.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ExecutionError;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.success.SuccessResult;
import com.zhongshi.sso.api.UserDetailsServiceRpc;
import com.zhongshi.sso.api.UserLogoutServiceRpc;
import com.zhongshi.tool.MapperUtils;
import com.zhongshi.tool.OkHttpClientUtil;
import com.zhongshi.tool.SQLType;
import com.zhongshi.user.FileInfo;
import com.zhongshi.user.TestDo;
import com.zhongshi.user.UserDo;
import com.zhongshi.user.api.UserServiceRest;
import com.zhongshi.uservo.LoginAndRegisterVo;
import com.zhongshi.uservo.UpdateUserVo;

import net.bytebuddy.asm.Advice.This;
import okhttp3.Response;


/**
 * 
 * 	功能说明：用户Rest API实现
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

public class UserServiceRestImpl extends UserServiceImpl implements UserServiceRest {
	
	@Resource
	private DiscoveryClient discoveryClient;
	
	@Reference
	protected UserDetailsServiceRpc userDetailsServicePRC;

	@Reference
	protected UserLogoutServiceRpc userLogoutService;

	@Resource
	protected BCryptPasswordEncoder passwordEncoder;
	
	
	// 用户注册
	@Override
	public AbstractBaseResult<UserDo> UserRegister(LoginAndRegisterVo loginAndRegisterVo, BindingResult bindingResult) {

		/* 校验参数 Begin */
		AbstractBaseResult result = loginAndRegisterVo.voHasErrors(bindingResult);
		if (!result.ifStatus()) {
			return result;
		}
		/* 校验参数 End */

		/* 查询用户是否存在 Begin */
		Integer userCount = count(UserDo.builder().username(loginAndRegisterVo.getUsername()).build());
		if (userCount > 0) {
			return error(code(40002));
		}
		/* 查询用户是否存在 End */

		/* Vo转换DO 添加数据库 Begin */
		UserDo user = loginAndRegisterVo.voToDo();
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Integer userInsert = insert(user, "");
		if (userInsert < 1) {
			return error(code(40004));
		}
		/* Vo转换DO 用户添加数据库 End */

		return success("注册成功");
	}

	
	
	
	// 用户登录
	@Override
	public AbstractBaseResult userLogin(LoginAndRegisterVo loginAndRegisterVo, BindingResult bindingResult) {

		/* 校验参数 Begin */
		AbstractBaseResult result = loginAndRegisterVo.voHasErrors(bindingResult);
		if (!result.ifStatus()) {
			return result;
		}
		/* 校验参数 End */

		/* PRC调用SSO登录 Begin */
		AbstractBaseResult userDetailsResult = userDetailsServicePRC.loadUserByUsername(loginAndRegisterVo.getUsername());
		if (!userDetailsResult.ifStatus()) {
			return userDetailsResult;
		}
		UserDetails userDetails = (UserDetails) ((SuccessResult) userDetailsResult).getData();
		if (!passwordEncoder.matches(loginAndRegisterVo.getPassword(), userDetails.getPassword())) {
			return error(code(40003));
		}
		/* PRC调用SSO登录 Begin */

		/* 采用OKhttp调用SSO获取Token Begin */
		Map<String, String> params = Maps.newHashMap();
		params.put("username", loginAndRegisterVo.getUsername());
		params.put("password", loginAndRegisterVo.getPassword()); 
		params.put("grant_type", "password");
		params.put("client_id", "client");
		params.put("client_secret", "secret");
		try {
			List<ServiceInstance> instances = discoveryClient.getInstances("zhongshi-etc-sso");
			Response response = OkHttpClientUtil.getInstance().postData(instances.get(SQLType.mathematics(1, instances.size())%instances.size()).getUri().toString()+"/auth/oauth/token", params);
			String jsonString = Objects.requireNonNull(response.body()).string();
			Map<String, Object> jsonMap = MapperUtils.json2map(jsonString);
			String token = String.valueOf(jsonMap.get("access_token"));
			if(token.equals("null")) {   
				return error(code(800));   
			}
			return success(new HashMap<String, String>() {
				{
					put("token", token);
				}
			});
		} catch (Exception e) {  
			e.printStackTrace();
			return error(code(600));
		}
		/* 采用OKhttp调用SSO获取Token End */

	}

	
	
	
	// 用户退出 
	@Override
	public AbstractBaseResult userLogout(HttpServletRequest request) {
		return userLogoutService.userLogout(request.getHeader("Authorization").replace("Bearer ", ""));
	}

	
	
	
	// Rest查询用户
	@Override
	public AbstractBaseResult<UserDo> getUserInfo() {
		UserDo user = userInfo();  
		return isNotNull(user) ? error(code(40010)) : success(user);
	}

	
	
	
	// 用户修改密码
	@Override
	public AbstractBaseResult userUpdatePassword(String password, String updatePassword, HttpServletRequest request) {

		/* 校验参数 Begin */
		if (isNotNull(password) || isNotNull(updatePassword)) {
			return error(code(40006));
		}
		/* 校验参数 End */

		/* 查询用户信息 Begin */
		UserDo userDo = userInfo();
		if (isNotNull(userDo)) {
			return error(code(40010));
		}
		/* 查询用户信息 End */

		/* 对比旧密码 Begin */
		if (!passwordEncoder.matches(password, userDo.getPassword())) {
			return error(code(40005));
		}
		/* 对比旧密码 End */

		/* 更新用户密码 Begin */
		userDo.setPassword(passwordEncoder.encode(updatePassword));
		Integer updateUserResult = update(userDo, userDo.getUsername());
		if (updateUserResult == 0) {
			return error(code(500));
		}
		/* 更新用户密码 End */

		/* 用户退出 Begin */
		userLogout(request);
		/* 用户退出 Begin */

		return success("用户密码修改成功!");
	}

	
	
	
	// 用户更新
	@Override
	public AbstractBaseResult userUpdate(UpdateUserVo userVo, BindingResult bindingResult) {

		/* 参数校验 Begin */
		AbstractBaseResult voHasErrors = userVo.voHasErrors(bindingResult);
		if (!voHasErrors.ifStatus()) {
			return voHasErrors;
		}
		/* 参数校验 End */

		/* 查询用户信息 Begin */
		UserDo userDo = userInfo();
		if (isNotNull(userDo)) {
			return error(code(40010));
		}
		/* 查询用户信息 End */

		/* 更新用户信息 Begin */
		UserDo user = userVo.voToDo();
		user.setId(userDo.getId());
		Integer updateUser = update(user, user.getUsername());
		if (updateUser < 1) {
			return error(code(7));
		}
		/* 更新用户信息 End */

		return success(user);
	}

	
	
	private String ENDPOINT = "oss-cn-shenzhen.aliyuncs.com";
    private String ACCESS_KEY_ID = "LTAIUEM4x1YOqT0O";
    private String ACCESS_KEY_SECRET = "XVRECYNWqS7uzssIXeNrcgKIamBjTh";
    private String BUCKET_NAME = "javasite";
	//用戶上传图片
	@Override
	public AbstractBaseResult upLoadFile(MultipartFile multipartFile) {
		
		/* 判断文件是否为空 Begin */
		if(isNotNull(multipartFile)) {
			return error(code(40009));
		}
		/* 判断文件是否为空 End */
		
		
		
		/* 图片上传oss Begin */
		String fileName = multipartFile.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String newName = UUID.randomUUID() + "." + suffix;
        String fileUrl = "http://" + BUCKET_NAME + "." + ENDPOINT + "/" + newName;
        OSS client = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        try {
            client.putObject(new PutObjectRequest(BUCKET_NAME, newName, new ByteArrayInputStream(multipartFile.getBytes())));
            // 上传文件路径 = http://BUCKET_NAME.ENDPOINT/自定义路径/fileName
        } catch (IOException e) {
            return error(code(40008));   
        } finally {
            client.shutdown();
        }
        /* 图片上传oss End */
        
        
        
        /* 查询用户信息 Begin */
		UserDo userDo = userInfo();
		if (isNotNull(userDo)) { 
			return error(code(40010));
		}
		/* 查询用户信息 End */
		
		
		

		/* 更新用户信息 Begin */
		userDo.setIcon(fileUrl); 
		Integer updateUser = update(userDo, userDo.getUsername());
		if (updateUser < 1) {
			return error(code(7));
		}  
		/* 更新用户信息 End */
		
		return success(new FileInfo(fileUrl));
    }
	
	
	@Override
	public AbstractBaseResult test() {
		//return success(this.baseMapper.UserToTest(new UserDo()));
		return success(this.baseMapper.selectUserToTest1(new UserDo()));
	}

}
