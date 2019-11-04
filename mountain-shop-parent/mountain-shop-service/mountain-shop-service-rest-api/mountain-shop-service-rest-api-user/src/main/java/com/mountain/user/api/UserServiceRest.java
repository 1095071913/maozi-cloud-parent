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

package com.mountain.user.api;

import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mountain.api.base.service.BaseService;
import com.mountain.factory.BaseResultFactory;
import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.user.UserDo;
import com.mountain.uservo.LoginAndRegisterVo;
import com.mountain.uservo.UpdateUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 	功能说明：领域模型Do
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-09-02 : 1:36:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

// @FeignClient(value = "mountain-shop-user")
@Api("User接口")
@RequestMapping("/user")
@RestController
public interface UserServiceRest extends BaseService<UserDo> {

	
	@PostMapping("/userRegister")
	@ApiOperation(value = "用户注册")
	default AbstractBaseResult<UserDo> UserRegister(@Validated @RequestBody LoginAndRegisterVo loginAndRegisterVo,BindingResult bindingResult){
		return BaseResultFactory.error(BaseResultFactory.code(4));
	}

	
	@PostMapping("/login")
	@ApiOperation(value = "用户登录")
	default AbstractBaseResult userLogin(@Validated @RequestBody LoginAndRegisterVo loginAndRegisterVo,BindingResult bindingResult) {
		return BaseResultFactory.error(BaseResultFactory.code(4));
	}

	
	@PostMapping("/logout")
	@ApiOperation(value = "用户退出")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "token", value = "token", required = false, paramType = "query", dataType = "String") 
	})
	default AbstractBaseResult userLogout(HttpServletRequest request) {
		return BaseResultFactory.error(BaseResultFactory.code(4));
	}
	
	
	@GetMapping("/info")
	@ApiOperation(value = "用户信息")
	default AbstractBaseResult userInfo() {
		return BaseResultFactory.error(BaseResultFactory.code(4));
	}
	
	
	@PostMapping("/updatePassword")
	@ApiOperation(value = "用户修改密码")
	default AbstractBaseResult userUpdatePassword(String password,String updatePassword,HttpServletRequest request) {
		return BaseResultFactory.error(BaseResultFactory.code(4));
	}
	
	
	@PostMapping("/updateUser")
	@ApiOperation(value = "用户修改信息")
	default AbstractBaseResult userUpdate(@Validated @RequestBody UpdateUserVo userVo,BindingResult bindingResult) {
		return BaseResultFactory.error(BaseResultFactory.code(4));
	}
	
	
}
