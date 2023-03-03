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

package com.maozi.factory.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.maozi.factory.BaseResultFactory;
import com.maozi.factory.result.error.ErrorResult;
import com.maozi.factory.result.error.exception.BusinessResultException;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("接口结果集")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractBaseResult<D> implements Serializable {

	private static final long serialVersionUID = -2733849641032268568L;

	@ApiModelProperty("HTTP状态码")
	protected Integer httpCode = 200;
	
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

	/**
	 * 结果类型map转实体类
	 * @param tClass
	 * @param <T>
	 * @return T
	 */
	public <T> T ifResultThrowErrorOrGetDataMapToBean(Class<T> tClass) {
		if(!isSuccess()) {
			throwBusinessError();
		}
		if (getData() instanceof Map){
			return BeanUtil.mapToBean((Map)getData(),tClass,true);
		}
		throw new BusinessResultException("data不为map",500);
	}

	/**
	 * 结果类型List转实体类集合
	 * @param tClass
	 * @param <T>
	 * @return List<T>
	 */
	public <T> List<T> ifResultThrowErrorOrGetDataMapsToBeans(Class<T> tClass) {
		if(!isSuccess()) {
			throwBusinessError();
		}
		if (getData() instanceof List){
			return BeanUtil.copyToList((List)getData(),tClass,null);
		}
		throw new BusinessResultException("data不为List",500);
	}
	
	public D ifResultThrowErrorOrGetNotNullData(String data) {
		if(!isSuccess()) {
			throwBusinessError();
		}
		
		if(BaseResultFactory.isNull(getData())) {
			throw new BusinessResultException(data+"不存在",404);
		}
		
		return getData();
	}
	
	public void throwBusinessError() {
		ErrorResult<D> result = getResult();
		throw new BusinessResultException(result,httpCode);
	}
	
	public void throwBusinessError(String message) {
		ErrorResult<D> result = getResult();
		throw new BusinessResultException(result,httpCode);
	}
	
	public AbstractBaseResult<D> setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
		return this;
	}
	
}