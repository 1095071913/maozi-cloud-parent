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
 */


package com.zhongshi.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.tool.MeiteBeanUtils;

import ch.qos.logback.core.net.LoginAuthenticator;
import lombok.Data;

/**
 * 
 * 	功能说明：领域模型Vo
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractBaseVomain<Do extends AbstractBaseDomain> implements Serializable {
	
	public Do voToDo() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return MeiteBeanUtils.voToDo(this,(Class<Do>)parameterizedType.getActualTypeArguments()[0]);
	}
	
	
	public AbstractBaseResult<Do> voHasErrors(BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
			// 如果参数有错误的话
			// 获取第一个错误!
			String errorMsg = bindingResult.getFieldError().getDefaultMessage();
			return BaseResultFactory.error(new CodeAttribute(1,errorMsg));
		}
		return BaseResultFactory.success("参数检验正确");
	}
	
}
