package com.maozi.mvc.config.rest;

import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.mvc.config.error.ErrorParamTranslation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestErrorHandler {

	/**
	 * 接口不存在
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Object handle404(NoHandlerFoundException e) {
		return ResultUtil.error(SystemErrorCode.NOT_RESOURCE_ERROR);
	}

	/**
	 * 参数校验失败
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Object handleValidException(MethodArgumentNotValidException e) {
		Map<String, String> errorMap = CollectionUtil.newHashMap();
		e.getFieldErrors().forEach(error ->
			errorMap.put(error.getField(), error.getDefaultMessage())
		);
		return ResultUtil.error(SystemErrorCode.PARAM_ERROR, errorMap);
	}

	/**
	 * 缺少请求参数异常
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingParam(MissingServletRequestParameterException e) {
		String paramName = e.getParameterName();
		String errorMsg = ObjectUtil.isNotNullEmpty(ErrorParamTranslation.errorParams.get(paramName)) ? ErrorParamTranslation.errorParams.get(paramName) + "不能为空" : paramName + "不能为空";

		return ResultUtil.error(SystemErrorCode.PARAM_ERROR).setMessage(errorMsg);
	}

	/**
	 * 请求体为空异常
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object handleAccessDeniedException(HttpMessageNotReadableException e) {
		return ResultUtil.error(SystemErrorCode.PARAM_ERROR);
	}

	/**
	 * 入参类型转换失败
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		return ResultUtil.error(SystemErrorCode.PARAM_ERROR);
	}

	/**
	 * 权限异常交还SpringSecurity处理
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public Object handleAccessDeniedException(AccessDeniedException e) {
		throw e;
	}

	/**
	 * 全局兜底异常
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Object handleGlobalException(Exception e) {
		ErrorCode errorCode = SystemErrorCode.SYSTEM_ERROR;
		log.error(errorCode.getMessage(), e);
		return ResultUtil.error(errorCode);
	}

}