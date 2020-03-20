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

package com.zhongshi.factory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zhongshi.base.AbstractBaseDomain;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.code.CodeHashMap;
import com.zhongshi.factory.result.error.ErrorResult;
import com.zhongshi.factory.result.success.SuccessResult;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 	功能说明：Result工厂
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-05：04:23:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@SuppressWarnings("serial")
@RefreshScope
@Service
@Data
@NoArgsConstructor
public class BaseResultFactory<T extends AbstractBaseDomain> implements Serializable {
	
	private static final long serialVersionUID = -4666437508651446479L;
	  
	protected static Map<String, CodeHashMap> codeDatas=new HashMap<>();
	   
	public static String applicationName;
	@Value("${spring.application.name}")
	public void setApplicationName(String applicationName) {
		BaseResultFactory.applicationName=applicationName;
	}
	
	
	
	public static Integer port;
	@Value("${application-port}")
	public void setPort(Integer port) {
		BaseResultFactory.port=port;
	}
	
	
	
	protected static String fuseMessage;
	@Value("${fuseMessage:错误熔断}")  
	public void setFuseMessage(String fuseMessage) {
		code(6).setMessage(fuseMessage); 
	}
	
	
	
	
	static {
		codeDatas.put("zhongshi-etc-base-code", new CodeHashMap("zhongshi-etc-base-code",0) {{
			
			this.put(new CodeAttribute(1,""));

			this.put(new CodeAttribute(2,"服务没有此code"));

			this.put(new CodeAttribute(3,"Token错误"));
			
			this.put(new CodeAttribute(6,"服务未启动"));
			
			this.put(new CodeAttribute(7,"更新失败"));
			
			this.put(new CodeAttribute(8,"添加失败"));
			
			this.put(new CodeAttribute(9,"删除失败"));
			
			this.put(new CodeAttribute(200,"成功"));
			
			this.put(new CodeAttribute(500,"内部服务错误"));
			
			this.put(new CodeAttribute(403,"权限不足"));
			
			this.put(new CodeAttribute(404,"没有此资源")); 
			  
			this.put(new CodeAttribute(600,"错误熔断")); 
			
			this.put(new CodeAttribute(700,"网关错误"));
			
			this.put(new CodeAttribute(800,"Redis未启动"));
			
		}});
	}
	
	
	
	
	
	
	
	
	   
	
	public static Boolean isNotNull(Object o){
		return StringUtils.isEmpty(o);
	}
	
	
	
	
	
	
	private static BaseResultFactory baseResultFactory;
	
	
	

	// 设置通用的响应
	private static HttpServletResponse response;
	
	
	

	public static BaseResultFactory getInstance() {
		if (baseResultFactory == null) {
			synchronized (BaseResultFactory.class) {
				if (baseResultFactory == null) {
					baseResultFactory = new BaseResultFactory();
				}
			}
		}
		
		
		BaseResultFactory.response = getResponse();
		// 设置通用响应
		baseResultFactory.initResponse();
		return baseResultFactory;
	}
	
	
	
	
	

	/**
	 * 初始化 HttpServletResponse
	 */
	private void initResponse() {
		// 需要符合 JSON API 规范
		if (ObjectUtils.allNotNull(response)) {
			response.setHeader("Content-Type", "application/vnd.api+json");
		}
	}
	
	
	
	
	

	private static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		return servletRequestAttributes.getRequest();
	}
	
	
	
	
	

	private static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (!ObjectUtils.allNotNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getResponse();
	}
	
	
	

	
	public AbstractBaseResult build(T attributes) {
		return new SuccessResult(attributes);
	}

	
	
	
	
	public AbstractBaseResult build(Object attributes) {
		return new SuccessResult(attributes);
	}


	
	public AbstractBaseResult build(Integer next, Integer last, List<T> attributes) {
		return new SuccessResult(next, last, attributes);
	}

	
	
	
	public AbstractBaseResult build(Long id,CodeAttribute codeData) {
		// 设置请求失败的响应码
		if (ObjectUtils.allNotNull(response)) {
			try {
				response.setStatus(codeData.getCode());
			}catch (Exception e) {
				response.setStatus(200);
			}
		}
		return new ErrorResult(id,codeData,applicationName);
	}
	
	
	public AbstractBaseResult build(CodeAttribute codeData) {
		return build(null,codeData);
	}
	
	
	

	public static AbstractBaseResult error(Long id,CodeAttribute codeData) {
		return BaseResultFactory.getInstance().build(id,codeData);
	}
	
	public static AbstractBaseResult error(CodeAttribute codeData) {
		return BaseResultFactory.getInstance().build(codeData);
	}
	
	
	
	

	public static <T extends AbstractBaseDomain> AbstractBaseResult success(Integer next, Integer last,
			List<T> attributes) {
		return BaseResultFactory.getInstance().build(next, last, attributes);
	}
	
	
	
	

	public static <T extends AbstractBaseDomain> AbstractBaseResult success(T attributes) {
		return BaseResultFactory.getInstance().build(attributes);
	}
	
	
	
	
	
	public static <T extends AbstractBaseDomain> AbstractBaseResult success(Object attributes) {
		return BaseResultFactory.getInstance().build(attributes);
	}
	
	
	
	protected static void code(CodeHashMap codeHashMap) {
		codeDatas.put(applicationName,codeHashMap);
	}
	
	public static CodeAttribute code(Integer code) {
		CodeAttribute returnResult=null;
		CodeAttribute baseCodeData = codeDatas.get("zhongshi-etc-base-code").get(code);
		if(isNotNull(baseCodeData)) {
			returnResult=codeDatas.get(applicationName).get(code);
		}else {
			returnResult=baseCodeData;
		}
		if(isNotNull(returnResult)) {
			returnResult=codeDatas.get("zhongshi-etc-base-code").get(2);
		}
		return returnResult;
	}
	
	
	public static AbstractBaseResult dubboFallback(Throwable ex) {
		StackTraceElement stackTraceElement = ex.getStackTrace()[0];
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.err.println("============  服务熔断策略     Begin    ==============");
		if(!isNotNull(authentication.getName())) {
			System.err.println("                                             "+"\r\n");
			System.err.println("触发异常用户："+authentication.getName()+"\r\n");
		}
		System.err.println("                                      ");
		System.err.println("服务异常地址："+stackTraceElement.toString());
		System.err.println("                                      ");
		System.err.println("服务异常信息："+ex.getLocalizedMessage());
		System.err.println("                                      ");
		System.err.println("============  服务熔断策略     End    ==============");
		return error(code(600));
	}
	
	
}
