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

package com.maozi.common.result.error;

import static com.maozi.common.BaseCommon.getRequest;
import static com.maozi.common.BaseCommon.isNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.code.CodeAttribute;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * 功能说明：结果集错误信息
 *
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 创建日期：2019-08-01 ：5:22:00
 *
 * 版权归属：蓝河团队
 *
 * 协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@NoArgsConstructor
@ApiModel("接口错误结果集")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ErrorResult<D> extends AbstractBaseResult<D> implements Serializable {
	
	@JsonIgnore
	@ApiModelProperty("HTTP状态码")
	protected Integer httpCode;
	
	@ApiModelProperty("错误内码")
	private Integer code;
	
	@ApiModelProperty("错误信息")
	private String message;
	
	@ApiModelProperty("错误数据")
	private D data;

	public ErrorResult(CodeAttribute<D> codeData) {
		
		code=codeData.getCode();
		data=codeData.getData();
		message=codeData.getMessage();
		
		this.setHttpCode(400);
		
	}
	
	public ErrorResult(Integer httpCode,CodeAttribute<D> codeData) {
		
		code=codeData.getCode();
		data=codeData.getData();
		message=codeData.getMessage();
		
		this.setHttpCode(httpCode);
		
	}

	@JsonIgnore
	public Boolean isCode(Integer... codes) {
		return Lists.newArrayList(codes).contains(code);
	}
	
	@JsonIgnore
	public Boolean isSystemError() {
		return code!=200 && code <= 1000;
	}

	@JsonIgnore
	public Boolean isBusinessError() {
		return code==400 || code > 1000;
	}
	
	@JsonIgnore
	public Boolean isPermissionsError() {
		return code==401 || code==403;
	}
	
	public AbstractBaseResult<D> setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
		return this;
	}
	
	public ErrorResult<D> autoIdentifyHttpCode() {
		
		if(!isNull(getRequest())) {
			BaseCommon.getResponse().setStatus(httpCode);
		}
		
		return this;
		
	}

}
