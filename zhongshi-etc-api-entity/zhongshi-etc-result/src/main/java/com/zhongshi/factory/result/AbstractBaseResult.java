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

package com.zhongshi.factory.result;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zhongshi.factory.result.error.ErrorResult;
import com.zhongshi.factory.result.error.exception.BusinessResultException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 功能说明：抽象结果集
 * <p>
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * <p>
 * 创建日期：2019-08-01 ：4:07:00
 * <p>
 * 版权归属：蓝河团队
 * <p>
 * 协议说明：Apache2.0（ 文件顶端 ）
 */

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseResult<D> implements Serializable {

	private static final long serialVersionUID = -2733849641032268568L;

	protected Integer httpCode = 200;
	
	public abstract D getData();
	
	public abstract Integer getCode();
	
	@JsonIgnore
	public Boolean isSuccess() {
		return getCode() == 200;
	}
	
	@JsonIgnore
	public <Result> Result getResult() {
		return (Result) this;
	}
	
	public <C> C ifResultThrowErrorOrGetSuccessResult() {
		if(!isSuccess()) {
			throwBusinessError();
		}
		return getResult();
	}
	
	public <C> C ifResultThrowErrorOrGetSuccessResult(Integer httpCode) {
		if(!isSuccess()) {
			this.httpCode=httpCode;
			throwBusinessError();
		}
		return getResult();
	}
	
	public D ifResultThrowErrorOrGetData() {
		if(!isSuccess()) {
			throwBusinessError();
		}
		return getData();
	}
	
	public void throwBusinessError() {
		ErrorResult<D> result = getResult();
		throw new BusinessResultException(result,httpCode);
	}
	
	public AbstractBaseResult<D> setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
		return this;
	}
	
}