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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.zhongshi.base.AbstractBaseDomain;
import com.zhongshi.dingding.DingDingMessage;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.code.CodeHashMap;
import com.zhongshi.factory.result.error.ErrorResult;
import com.zhongshi.factory.result.success.SuccessResult;
import com.zhongshi.sso.OauthUserDetails;
import com.zhongshi.tool.ApplicationEnvironmentConfig;
import com.zhongshi.tool.MapperUtils;
import cn.hutool.extra.cglib.CglibUtil;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@DubboService
@NoArgsConstructor
public class BaseResultFactory implements Serializable {

	private static final long serialVersionUID = -4666437508651446479L;

	public static final Logger log = LoggerFactory.getLogger(BaseResultFactory.class);

	public static FastDateFormat DETAULT_DATE_FORMAT = FastDateFormat.getInstance("yyy-MM-dd HH:mm:ss");

	public static final String BasicCode = "zhongshi-etc-base-code";

	public static Map<String, CodeHashMap> codeDatas = new HashMap<>();

	public static ConcurrentHashMap<String, Map<String, String>> adminHealthError = new ConcurrentHashMap<String, Map<String, String>>();

	private static BaseResultFactory baseResultFactory;

	public static ThreadLocal<String> sql = new ThreadLocal<String>();
	
	public static AsyncRestTemplate asyncRestTemplate;
	
	public static DiscoveryClient discoveryClient;
	
	@Autowired(required = false)
	public BaseResultFactory(AsyncRestTemplate asyncRestTemplate,DiscoveryClient discoveryClient) {
		
		this.asyncRestTemplate = asyncRestTemplate;
		
		this.discoveryClient = discoveryClient;
	}
	
	protected String serviceName;
	
	protected void setServiceName(String serviceName) {
		this.serviceName=ApplicationEnvironmentConfig.applicationName+"-service"+"-"+serviceName;
	}

	public CodeAttribute code(Integer code) {
		return getCode(null, code);
	}

	public CodeAttribute code(String key, Integer code) {
		return getCode(key, code);
	}

	private CodeAttribute getCode(String key, Integer code) {

		CodeAttribute returnResult = null;

		if (isNull(key) && code >= 1 && code < 1000) {
			returnResult = codeDatas.get(BasicCode).get(code);
		}

		if (isNull(key)) {
			key = serviceName;
		}

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(key+"-code").get(code);
		}

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(BasicCode).get(2);
		}

		return returnResult;
	}

	public CodeAttribute code(String key, String valueKey) {
		
		if(isNull(key)) {
			key=serviceName;
		}else {
			key=ApplicationEnvironmentConfig.applicationName+"-service-"+key;
		}

		CodeAttribute returnResult = codeDatas.get(key+"-code").get(valueKey);

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(BasicCode).get(2);
		}

		return returnResult;
	}
	
	public static CodeAttribute baseCode(Integer code) {
		
		CodeAttribute returnResult = codeDatas.get(BasicCode).get(code);

		if (isNull(returnResult)) {
			returnResult = codeDatas.get(BasicCode).get(2);
		}
		
		return returnResult;
	}

	public static Boolean isNull(Object o) {
		return StringUtils.isEmpty(o);
	}

	public static Boolean isNotNull(Object o) {
		return !StringUtils.isEmpty(o) && !o.equals("null");
	}
	
	public static <T> T copy(Object data,Class<T> targetClass){
		return CglibUtil.copy(data, targetClass);
	}
	
	public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target){
		return CglibUtil.copyList(source, target);
	}
	
	public static <T> String column(SFunction<T, ?> column){
		return PropertyNamer.methodToProperty(LambdaUtils.extract(column).getImplMethodName());
	}	

	private void initResponse() {
		HttpServletResponse response = getResponse();
		if (ObjectUtils.allNotNull(response)) {
			response.setHeader("Content-Type", "application/vnd.api+json");
		}
	}

	public static BaseResultFactory getInstance() {
		if (baseResultFactory == null) {
			synchronized (BaseResultFactory.class) {
				if (baseResultFactory == null) {
					baseResultFactory = new BaseResultFactory();
				}
			}
		}
		baseResultFactory.initResponse();
		return baseResultFactory;
	}

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (!ObjectUtils.allNotNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getRequest();
	}

	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (!ObjectUtils.allNotNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getResponse();
	}

	public SuccessResult build(Object attributes) {
		return new SuccessResult(attributes);
	}

	public ErrorResult build(CodeAttribute codeData) {
		return new ErrorResult(codeData);
	}

	public ErrorResult build(CodeAttribute codeData, Integer httpCode) {
		return new ErrorResult(httpCode,codeData);
	}

	public static ErrorResult error(CodeAttribute codeData) {
		return BaseResultFactory.getInstance().build(codeData);
	}

	public static ErrorResult error(CodeAttribute codeData, Integer httpCode) {
		return BaseResultFactory.getInstance().build(codeData, httpCode);
	}

	public static <T extends AbstractBaseDomain> SuccessResult success(T attributes) {
		return BaseResultFactory.getInstance().build(attributes);
	}

	public static SuccessResult success(Object attributes) {
		return BaseResultFactory.getInstance().build(attributes);
	}

	public static String httpDiscoveryServer(String serverName) {
		List<ServiceInstance> instances = discoveryClient.getInstances(serverName);
		if (instances.size() > 0) {
			return instances.get(ThreadLocalRandom.current().nextInt(instances.size())).getUri().toString();
		}
		return null;
	}

	public static String rpcDiscoveryServer(String serverName) {
		List<ServiceInstance> instances = discoveryClient.getInstances(serverName);
		if (instances.size() <= 0) {
			return null;
		}
		ServiceInstance serviceInstance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
		return "dubbo://" + serviceInstance.getHost() + ":20880";
	}

	public static String getCurrentUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (isNull(authentication) || "anonymousUser".equals(authentication.getName())) {
			return "游客";
		}
		return authentication.getName();
	}

	public static OauthUserDetails getOauthUserDetails() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (isNull(authentication)) {
			return null;
		}

		if (authentication.getPrincipal() instanceof String) {
			return null;
		}

		return (OauthUserDetails) authentication.getPrincipal();
	}

	public static <U> U getOauthUserDetail(Class<U> userClass) {

		OauthUserDetails oauthUserDetails = getOauthUserDetails();
		if (isNull(oauthUserDetails)) {
			return null;
		}
		Map<String, Map<String, Object>> userInfos = oauthUserDetails.getUserInfos();

		return MapperUtils.map2pojo(userInfos.get(userClass.getName()), userClass);
	}

	public static <U> U getOauthUserDetail(String key, Class<U> userClass) {

		if (isNull(key)) {
			return null;
		}

		OauthUserDetails oauthUserDetails = getOauthUserDetails();
		if (isNull(oauthUserDetails)) {
			return null;
		}
		Map<String, Map<String, Object>> userInfos = oauthUserDetails.getUserInfos();

		return MapperUtils.map2pojo(userInfos.get(key), userClass);
	}

	public static StringBuilder appendLog(Map<String, String> logs) {

		StringBuilder sb = new StringBuilder();

		for (String key : logs.keySet()) {
			sb.append("[ " + key + "：" + logs.get(key) + " ]  ");
		}
		sb.delete(0, 2);
		sb.delete(sb.length() - 4, sb.length());

		return sb;
	}

	public static void errorAlarm(String key, Map<String, String> errorLogs) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[ TID ]"+"："+getTraceId()+"\r\n");
		sb.append("[ APP ]"+"："+ApplicationEnvironmentConfig.applicationName+"\r\n");
		sb.append("[ TIME ]"+"："+DETAULT_DATE_FORMAT.format(new Date())+"\r\n");
        
        for(String logKey:errorLogs.keySet()) {
        	sb.append("[ "+logKey+" ]"+"："+errorLogs.get(logKey)+"\r\n");
        }
		
        adminHealthError.put(key, errorLogs);
        
		ActiveSpan.tag("error", sb.toString());
		
		DingDingMessage message = new DingDingMessage(sb.toString());
    	HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<String>(JSONObject.toJSONString(message).toString(), headers);
        asyncRestTemplate.postForEntity("https://oapi.dingtalk.com/robot/send?access_token="+ApplicationEnvironmentConfig.dingdingToken,formEntity,String.class);
        
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

	public static void debug(String message) {
		log.debug(message);
		ActiveSpan.debug(message);
	}

	public static void info(String message) {
		log.info(message);
		ActiveSpan.info(message);
	}

	public static void error(String message) {
		log.error(message);
		ActiveSpan.error(message);
	}

	public static String getTraceId() {
		String traceId = TraceContext.traceId();
		if (isNull(traceId)) {
			return null;
		}
		if (traceId.equals("Ignored_Trace")) {
			return null;
		}
		return traceId;
	}

	@Trace(operationName = "入参值")
	@Tags({ @Tag(key = "入参值", value = "arg[0]") })
	public void functionParam(Object param) {
	}

	@Trace(operationName = "返回值")
	@Tags({ @Tag(key = "返回值", value = "arg[0]") })
	public void functionReturn(Object result) {
	}

	@Trace(operationName = "错误值")
	@Tags({ @Tag(key = "错误值", value = "arg[0]") })
	public void functionError(Object errorMessage) {
	}

	public static void clean() {sql.remove();}

}