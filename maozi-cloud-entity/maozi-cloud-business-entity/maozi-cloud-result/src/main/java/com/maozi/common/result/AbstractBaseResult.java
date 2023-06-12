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

package com.maozi.common.result;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.exception.BusinessResultException;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 功能说明：抽象结果集
 * 
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * 
 * 创建日期：2019-08-01 ：4:07:00
 * 
 * 版权归属：蓝河团队
 * 
 * 协议说明：Apache2.0（ 文件顶端 ）
 */

@Data
@SuperBuilder
@NoArgsConstructor
@ApiModel("接口结果集")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseResult<D> implements Serializable {
	
	@ApiModelProperty("数据")
	public abstract D getData();
	
	@ApiModelProperty("业务内码")
	public abstract Integer getCode();
	
	@JsonIgnore
	public Boolean isSuccess() {
		return getCode() == 200;
	}
	
	@JsonIgnore
	public <Result> Result getResult() {
		return (Result) this;
	}
	
	@JsonIgnore
	public <C> C getSuccessResultThrowError() {
		if(!isSuccess()) {
			throwError();
		}
		return getResult();
	}
	
	@JsonIgnore
	public D getResultDataThrowError() {
		if(!isSuccess()) {
			throwError();
		}
		return getData();
	}
	
	@JsonIgnore
	public D getResultNotNullDataThrowError(String message) {
		
		if(!isSuccess()) {
			throwError();
		}
		
		if(BaseCommon.isNull(getData())) {
			throw new BusinessResultException(message+"不存在",404);
		}
		
		return getData();
		
	}
	
	private void throwError() {
		ErrorResult<D> result = getResult();
		throw new BusinessResultException(result);
	}
	
}