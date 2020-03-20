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

package com.zhongshi.factory.result.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhongshi.base.AbstractBaseDomain;
import com.zhongshi.factory.result.AbstractBaseResult;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 	功能说明：结果集成功信息
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-01 ：4:06:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SuccessResult<T extends AbstractBaseDomain> extends AbstractBaseResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2336441473805533324L;
	private List<DataBean<T>> databeans ;
	private Object data;

	/**
	 * 请求的结果（单笔）
	 * 
	 * @param self
	 *            当前请求路径
	 * @param attributes
	 *            领域模型
	 */
	public SuccessResult(T data) {
		databeans=new ArrayList<DataBean<T>>();
		createDataBean(data);
	}
	
	public SuccessResult(Object data) {
		this.data=data;
	}
	
	
	/**
	 * 请求的结果（分页）
	 * 
	 * @param self
	 *            当前请求路径
	 * @param next
	 *            下一页的页码
	 * @param last
	 *            最后一页的页码
	 * @param attributes
	 *            领域模型集合
	 */
	public SuccessResult(Integer next, Integer last, List<T> attributes) {
		databeans=new ArrayList<DataBean<T>>();
		String self = getRequest().getRequestURI();
		attributes.forEach(attribute -> {
			Links links=new Links(self+"?page="+next
								, self+"?page="+next
								, self+"?page="+next);
			createDataBean(attribute).setLinks(links);
		});
	}

	private DataBean<T> createDataBean(T data) {
		DataBean<T> dataBean = new DataBean<T>(data.getClass().getSimpleName(), data.getId(), data, null);
		databeans.add(dataBean);
		return dataBean;
	}
	
	

	private static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		return servletRequestAttributes.getRequest();
	}

}