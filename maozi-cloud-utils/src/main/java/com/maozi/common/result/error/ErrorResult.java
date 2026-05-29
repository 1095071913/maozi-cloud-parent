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
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "接口错误结果集")
public class ErrorResult<D> extends AbstractBaseResult<D> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@Schema(description = "HTTP状态码")
	protected Integer httpCode;
	
	@Schema(description = "错误内码")
	private Integer code;
	
	@Schema(description = "错误信息")
	private String message;

	@JsonIgnore
	@Schema(description = "异常错误信息")
	private String exceptionMessage;
	
	@Schema(description = "错误数据")
	private D data;

	public ErrorResult(String message) {
		this.message = message;
	}

	public ErrorResult(Integer code, String message) {
		this(message);
		this.code = code;
	}

	public ErrorResult(Integer code, String message, D data) {
		this(code,message);
		this.data = data;
	}

	public ErrorResult(ErrorCode errorCode) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.exceptionMessage = errorCode.getExceptionMessage();
	}

	public ErrorResult(ErrorCode errorCode,D errorData) {
		this(errorCode);
		this.data = errorData;
	}

	@Override
	public Integer getCode() {
		return ObjectUtil.isNotNullEmpty(code) ? code : SystemErrorCode.BUSINESS_ERROR_DEFAULT_CODE;
	}

	public Integer getHttpCode() {
		return ObjectUtil.isNotNullEmpty(httpCode) ? httpCode : SystemErrorCode.BUSINESS_ERROR_DEFAULT_CODE;
	}

	public String getExceptionMessage() {
		return ObjectUtil.isNotNullEmpty(exceptionMessage) ? exceptionMessage : message;
	}

	@JsonIgnore
	public Boolean isCode(Integer... codes) {
		return CollectionUtil.newArrayList(codes).contains(code);
	}
	
	@JsonIgnore
	public Boolean isSystemError() {
		return !isBusinessError();
	}

	@JsonIgnore
	public Boolean isBusinessError() {
		return SystemErrorCode.MAX_SYSTEM_ERROR_CODE < code;
	}
	
	@JsonIgnore
	public Boolean isPermissionsError() {
		return Objects.equals(SystemErrorCode.PERMISSION_ERROR.getCode(),code) || Objects.equals(SystemErrorCode.USER_AUTH_ERROR.getCode(),code);
	}

	public ErrorResult<D> setMessage(String message) {
		this.message = message;
		return this;
	}

	public ErrorResult<D> setMessage(String message,String exceptionMessage) {
		this.message = message;
		this.exceptionMessage = exceptionMessage;
		return this;
	}

	public ErrorResult<D> setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
		return this;
	}

	public ErrorResult<D> setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
		return this;
	}

	public ErrorResult<D> autoIdentifyHttpCode() {

		HttpServletResponse response = WebUtil.getResponse();
		if(ObjectUtil.isNotNullEmpty(response)) {
			response.setStatus(getHttpCode());
		}
		
		return this;
		
	}

	public ErrorResult<D> autoIdentifyHttpCode(Integer httpCode) {

		this.setHttpCode(httpCode);

		autoIdentifyHttpCode();

		return this;

	}

}
