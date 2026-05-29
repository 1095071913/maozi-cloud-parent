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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maozi.common.ObjectUtil;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.common.result.success.SuccessResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(description = "接口响应结果集")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseResult<D> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "数据")
	public abstract D getData();
	
	@Schema(description = "业务内码")
	public abstract Integer getCode();
	
	@JsonIgnore
	public Boolean isSuccess() {
		return getCode() == 200;
	}

	@JsonIgnore
	public Boolean isError() {
		return !isSuccess();
	}
	
	@JsonIgnore
	public SuccessResult<D> getSuccessResult() {
		return this instanceof SuccessResult<D> successResult ? successResult : null;
	}

	@JsonIgnore
	public ErrorResult<D> getErrorResult() {
		return this instanceof ErrorResult<D> errorResult ? errorResult : null;
	}
	
	@JsonIgnore
	public D getResultDataThrowError() {
		if(isError()) throwError();
		return getData();
	}
	
	@JsonIgnore
	public D getResultNotNullDataThrowError(String resourceName) {
		
		if(isError()) throwError();
		
		if(ObjectUtil.isNullEmpty(getData())) {
			throw new BusinessResultException(SystemErrorCode.DATA_NOT_EXIST_ERROR).setResource(resourceName);
		}
		
		return getData();
		
	}
	
	private void throwError() {
		throw new BusinessResultException(getErrorResult());
	}
	
}