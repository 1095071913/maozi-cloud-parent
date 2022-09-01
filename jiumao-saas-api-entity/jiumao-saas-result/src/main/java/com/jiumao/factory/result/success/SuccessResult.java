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

package com.jiumao.factory.result.success;

import java.io.Serializable;

import com.jiumao.factory.result.AbstractBaseResult;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 功能说明：结果集成功信息
 * <p>
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * <p>
 * 创建日期：2019-08-01 ：4:06:00
 * <p>
 * 版权归属：蓝河团队
 * <p>
 * 协议说明：Apache2.0（ 文件顶端 ）
 */

@Data
@NoArgsConstructor
@ApiModel("接口成功结果集")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class SuccessResult<D> extends AbstractBaseResult<D> implements Serializable {

	private static final long serialVersionUID = -2336441473805533324L;
	
	private D data;
	
	public SuccessResult(D data) {
		this.data=data;
	}
	
	@Override
	public Integer getCode() {
		return this.httpCode;
	}
	
}