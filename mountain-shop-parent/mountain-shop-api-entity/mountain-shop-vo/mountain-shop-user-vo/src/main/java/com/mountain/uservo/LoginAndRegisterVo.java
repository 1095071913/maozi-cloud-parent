
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

package com.mountain.uservo;

import javax.validation.constraints.NotNull;
import com.mountain.base.AbstractBaseDomain;
import com.mountain.base.AbstractBaseVomain;
import com.mountain.user.UserDo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 
 * 	功能说明：登录与注册VO
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-01 ：6:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@ApiModel(value ="LoginAndRegisterVo",description="登录与注册")
public class LoginAndRegisterVo extends AbstractBaseVomain<UserDo>{
	
	
	
//    @ApiModelProperty(value = "手机号码", required = true)
//	@NotNull(message = "手机号不能为空!")
//	@Size(min = 11, max = 11, message = "手机号码长度不正确")
//	@Pattern(regexp = "^(((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8})$", message = "手机号格式错误")
//	private String phone;
    
    
	
	@ApiModelProperty(value = "用户名", required = true)
	@NotNull(message = "用户名不能为空")
	private String username;
	
	
    @ApiModelProperty(value = "密码", required = true)
	@NotNull(message = "密码不能为空!")
	private String password;

    
	
//	@ApiModelProperty(value = "注册码", required = true)
//	@NotNull(message = "注册码不能为空!")
//	private String code;
}
