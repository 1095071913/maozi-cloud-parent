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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.maozi.base.CodeData;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static com.maozi.common.BaseCommon.getRequest;
import static com.maozi.common.BaseCommon.isNull;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "接口错误结果集")
public class ErrorResult<D> extends AbstractBaseResult<D> implements Serializable {
	
	@JsonIgnore
	@Schema(description = "HTTP状态码")
	protected Integer httpCode;
	
	@Schema(description = "错误内码")
	private Integer code;
	
	@Schema(description = "错误信息")
	private String message;
	
	@Schema(description = "错误数据")
	private D data;

	public ErrorResult(CodeData<D> codeData) {
		
		code = codeData.getCode();
		data = codeData.getData();
		message = codeData.getMessage();
		
		this.setHttpCode(400);
		
	}

	public ErrorResult(String serviceName,CodeData<D> codeData) {

		code = codeData.getCode();
		data = codeData.getData();
		message = serviceName + codeData.getMessage();

		this.setHttpCode(400);

	}

	public ErrorResult(String serviceName,CodeData<D> codeData,Integer httpCode) {

		code = codeData.getCode();
		data = codeData.getData();
		message = serviceName + codeData.getMessage();

		this.setHttpCode(httpCode);

	}
	
	public ErrorResult(Integer httpCode, CodeData<D> codeData) {
		
		code = codeData.getCode();
		data = codeData.getData();
		message = codeData.getMessage();
		
		this.setHttpCode(httpCode);
		
	}

	public ErrorResult(CodeData<D> codeData,D errorData) {

		code = codeData.getCode();
		data = errorData;
		message = codeData.getMessage();

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
