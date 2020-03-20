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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhongshi.base.AbstractBaseDomain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 	功能说明：抽象结果集
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-01 ：4:07:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@NoArgsConstructor
public abstract class AbstractBaseResult<T extends AbstractBaseDomain> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2733849641032268568L;
	public static final Integer SUCCESS_CODE = 200;
	public static final String LEVEL="DEBUG";

	protected Integer code = SUCCESS_CODE;
     
	/**
	 * 此为内部类
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected static class Links implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3309321116399623841L;
		private String selfURL;
		private String nextURL;
		private String lastURL;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DataBean<T extends AbstractBaseDomain> implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8532788182597548267L;
		private String type;
		private Long id;
		private T data;
		private Links links;
	}

	/**
	 * 校验是否成功and失败
	 * 
	 * @return
	 */ 
	public Boolean ifStatus() {
		return SUCCESS_CODE.equals(this.code)   ? true : false;
	}

}