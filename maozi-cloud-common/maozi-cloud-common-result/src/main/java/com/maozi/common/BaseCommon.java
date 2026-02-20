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

package com.maozi.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlWrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maozi.base.BaseEnum;
import com.maozi.base.CodeData;
import com.maozi.base.enums.EnvironmentType;
import com.maozi.base.error.code.SystemErrorCode;
import com.maozi.base.param.ValidCollectionParam;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.common.result.success.SuccessResult;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import com.maozi.utils.context.ApplicationLinkContext;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;


@Data
public class BaseCommon implements Serializable {

	private final static String DEFAULT_TID = "Ignored_Trace";

	public static final Logger log = LoggerFactory.getLogger(BaseCommon.class);

	protected String getAbbreviationModelName() { return "组件"; }

    public static TransmittableThreadLocal<StringBuilder> sql = new TransmittableThreadLocal<>();

	private static Validator validator =  Validation.buildDefaultValidatorFactory().getValidator();

	public static ConcurrentHashMap<String, Map<String, String>> adminHealthError = new ConcurrentHashMap<>();

	public static Boolean isNull(Object data) {
		return Objects.isNull(data);
	}

	public static Boolean isNotNull(Object data) {
		return Objects.nonNull(data);
	}

	public static Boolean isEmpty(String data) {
		return StringUtils.isEmpty(data);
	}

	public static Boolean isNotEmpty(String data) {
		return StringUtils.isNotEmpty(data);
	}

	public static Boolean collectionIsNotEmpty(Collection<?> data) {
		return CollectionUtil.isNotEmpty(data);
	}

	public static Boolean collectionIsEmpty(Collection<?> data) {
		return CollectionUtil.isEmpty(data);
	}

	public static void checkBoolThrowError(Boolean bool,String message) {
		if(!bool) {
			throw new BusinessResultException(400,message,200);
		}
	}

	public static void checkBoolThrowError(Boolean bool,String serviceName,CodeData errorCode) {
		if(!bool) {
			throw new BusinessResultException(serviceName,errorCode,200);
		}
	}

	public static <T> T isNullThrowError(T data,String serviceName) {

		if(isNull(data)) {
			throw new BusinessResultException(serviceName,SystemErrorCode.DATA_NOT_EXIST_ERROR,200);
		}

		return data;

	}

	public static String isEmptyThrowError(String data,String serviceName) {

		if(isEmpty(data)) {
			throw new BusinessResultException(serviceName,SystemErrorCode.DATA_NOT_EXIST_ERROR,200);
		}

		return data;

	}

	public static <T> Collection<T> collectionIsEmptyThrowError(Collection<T> datas,String serviceName) {

		if(collectionIsEmpty(datas)) {
			throw new BusinessResultException(serviceName,SystemErrorCode.DATA_NOT_EXIST_ERROR,200);
		}

		return datas;
	}

	public static <T> Collection<T> listNullAssignment(Collection<T> data) {
		return isNull(data) ? Lists.newArrayList() : data;
	}

	public static void validate(@Valid Object obj){

		if (obj instanceof Collection){

			obj = ValidCollectionParam.builder().list((Collection) obj).build();

		}

		Map<String, String> errors = Maps.newHashMap();

		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);

		for (ConstraintViolation<Object> error : constraintViolations) {
			errors.put(error.getPropertyPath().toString(), error.getMessage());
		}

		if(collectionIsNotEmpty(constraintViolations)) {
			throw new BusinessResultException(SystemErrorCode.PARAM_ERROR,errors,200);
		}

	}


	public static <T> Consumer<T> wrapConsumer(Consumer<T> consumer) {

		if (consumer == null) {
			return null;
		}

		consumer.andThen((value)->{
			clearContext();
		});

		return TtlWrappers.wrapConsumer(consumer);

	}


	public static <T> T copy(Object data,Class<T> targetClass){

		if(isNull(data)) {
			return null;
		}

		return CglibUtil.copy(data, targetClass);

	}

	public static <S, T> List<T> copyList(Collection<S> source, Supplier<T> target){

		if(collectionIsEmpty(source)) {
			return Lists.newArrayList();
		}

		return CglibUtil.copyList(source, target);

	}

	public static <T extends BaseEnum> T getEnum(Integer value,Class<T> clazz) {

		T[] values = clazz.getEnumConstants();

		for(T iEnum : values) {

			if(iEnum.getValue().intValue() == value.intValue()) {
				return iEnum;
			}

		}

		return null;

	}

	public static <T extends BaseEnum> T getEnumNullThrow(Integer value,Class<T> clazz,String serviceName) {

		T[] values = clazz.getEnumConstants();

		for(T iEnum : values) {

			if(iEnum.getValue().intValue() == value.intValue()) {
				return iEnum;
			}

		}

		throw new BusinessResultException(serviceName,SystemErrorCode.DATA_NOT_EXIST_ERROR,200);

	}

	private static void initResponse() {
		HttpServletResponse response = getResponse();
		if (isNotNull(response)) {
			response.setHeader("Content-Type", "application/vnd.api+json");
		}
	}

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (isNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getRequest();
	}

	public static HttpServletResponse getResponse() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (isNull(servletRequestAttributes)) {
			return null;
		}
		return servletRequestAttributes.getResponse();
	}

	public static <T> ErrorResult<T> error(CodeData<T> codeData) {
		initResponse();
		return new ErrorResult<>(codeData);
	}

	public static <T> ErrorResult<T> error(String serviceName,CodeData<T> codeData) {
		initResponse();
		return new ErrorResult<>(serviceName,codeData);
	}

	public static <T> ErrorResult<T> error(String serviceName,CodeData<T> codeData, Integer httpCode) {
		initResponse();
		return new ErrorResult<>(serviceName,codeData,httpCode);
	}

	public static <T> ErrorResult<T> error(CodeData<T> codeData, Integer httpCode) {
		initResponse();
		return new ErrorResult<>(httpCode,codeData);
	}

	public static <T> ErrorResult<T> error(CodeData<T> codeData,T errorData) {
		initResponse();
		return new ErrorResult<>(codeData,errorData);
	}

	public static <T> ErrorResult<T> error(CodeData<T> codeData,T errorData,Integer httpCode) {
		initResponse();
		return new ErrorResult<>(codeData,errorData);
	}

	public static <T> SuccessResult<T> success() {
		initResponse();
		return new SuccessResult<>();
	}

	public static <T> SuccessResult<T> success(T attributes) {
		initResponse();
		return new SuccessResult<>(attributes);
	}

	public static String getCurrentUserName() {
		return ApplicationLinkContext.USERNAMES.get();
	}

	public static List<String> getPermissions() {

		List<String> permissions = Lists.newArrayList();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (isNull(authentication) || !authentication.isAuthenticated()) {
			return permissions;
		}

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		authorities.stream().forEach((permission)->{
			permissions.add(permission.getAuthority());
		});

		return permissions;

	}

	public static String getCurrentClientId() {

		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		if (isNull(authentication) || !authentication.isAuthenticated()) {
			return "visitor";
		}

		return authentication.getOAuth2Request().getClientId();

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
		sb.append("[ TIME ]"+"："+ DatePattern.NORM_DATETIME_FORMAT.format(new Date())+"\r\n");

		for(String logKey:errorLogs.keySet()) {
			sb.append("[ "+logKey+" ]"+"："+errorLogs.get(logKey)+"\r\n");
		}

		adminHealthError.put(key, errorLogs);

		ActiveSpan.tag("error", sb.toString());

		//TODO 请求机器人发送告警消息

	}

	public static Boolean isEnvironment(EnvironmentType type) {
		return type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
	}

	public static Boolean notEnvironment(EnvironmentType type) {
		return ! type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
	}

	public static String getVersionDefault(Object version){
		return isNotNull(version) && isNotEmpty(version.toString()) ? version.toString() : ApplicationLinkContext.APPLICATION_DEFAULT_VERSION;
	}

	public static String getStackTrace(Throwable t) {

		StringWriter sw = new StringWriter();

		PrintWriter pw = new PrintWriter(sw);

		t.printStackTrace(pw);

		return sw.toString();

	}

	public static void throwSystemError(Throwable t) {

		error(getStackTrace(t));

		throw new BusinessResultException(SystemErrorCode.SYSTEM_ERROR,200);

	}

	public static void throwSystemError(String message) {

		error(message);

		throw new BusinessResultException(SystemErrorCode.SYSTEM_ERROR,200);

	}

	public static void throwSystemError(Boolean bool,String message) {

		if(!bool){

			error(message);

			throw new BusinessResultException(SystemErrorCode.SYSTEM_ERROR,200);

		}

	}

	public static void log(Boolean error,String message){

		if(!error){
			info(message);
		}else{
			error(message);
		}

	}

	public static void log(Boolean error,Map<String, String> logs){

		String message = appendLog(logs).toString();

		if(!error){
			info(message);
		}else{
			error(message);
		}

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

	public static void info(Map<String, String> logs) {

		String message = appendLog(logs).toString();

		log.info(message);

		ActiveSpan.info(message);

	}

	public static void error(Map<String, String> logs) {

		String message = appendLog(logs).toString();

		log.error(message);

		ActiveSpan.error(message);

	}

	public static void error(Throwable e) {

		String message = getStackTrace(e);

		log.error(message);

		ActiveSpan.error(message);

	}

	public static String getTraceId() {

		String traceId = TraceContext.traceId();

		if (isNull(traceId)) {
			return null;
		}

		if (DEFAULT_TID.equals(traceId)) {
			return null;
		}

		return traceId;

	}

	public static void clearContext() {

		BaseCommon.sql.remove();

		ApplicationLinkContext.clear();

	}

	@Trace(operationName = "入参值")
	@Tags({ @Tag(key = "入参值", value = "arg[0]") })
	public void functionParam(Object param) {}

	@Trace(operationName = "返回值")
	@Tags({ @Tag(key = "返回值", value = "arg[0]") })
	public void functionReturn(Object result) {}

	@Trace(operationName = "错误值")
	@Tags({ @Tag(key = "错误值", value = "arg[0]") })
	public void functionError(Object errorMessage) {}

}