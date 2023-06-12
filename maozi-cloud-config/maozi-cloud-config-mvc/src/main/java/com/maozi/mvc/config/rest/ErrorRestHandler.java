package com.maozi.mvc.config.rest;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.google.common.collect.Maps;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.code.CodeAttribute;
import com.maozi.mvc.config.error.ErrorParamTranslation;

@ControllerAdvice
public class ErrorRestHandler extends ResponseEntityExceptionHandler {

//	@Override
//	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {
//		return super.handleExceptionInternal(ex, BaseResultFactory.error(BaseResultFactory.code(15), 405), headers,status, request);
//	}
//
//	@Override
//	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {
//		return super.handleExceptionInternal(ex, BaseResultFactory.error(BaseResultFactory.code(15), 405), headers,status, request);
//	}
//
//	@Override
//	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {
//		return handleExceptionInternal(ex,BaseResultFactory.error(BaseResultFactory.code(400)) , headers, status, request);
//	}
	
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}
		
		if(ex instanceof MethodArgumentNotValidException) {
			
			MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
			
			Map<String,String> errorMessages = Maps.newHashMap();
			
			methodArgumentNotValidException.getBindingResult().getFieldErrors().stream().forEach(fieldError -> errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage()));
			
			return new ResponseEntity<>(BaseCommon.error(new CodeAttribute<Map<String,String>>(400,"参数错误",errorMessages),status.value()), headers, status);
		
		}else if (ex instanceof MissingServletRequestParameterException) {
			
			MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) ex;
			
			String paramErrorMessage = ErrorParamTranslation.errorParams.get(missingServletRequestParameterException.getParameterName());
			
			if(BaseCommon.isNull(paramErrorMessage)) {
				paramErrorMessage=missingServletRequestParameterException.getParameterName();
			}
			
			return new ResponseEntity<>(BaseCommon.error(new CodeAttribute(400,missingServletRequestParameterException.getParameterName()+"不能为空"),status.value()), headers, status);
			
		}else {
			BaseCommon.error(ex.getLocalizedMessage()); 
		}
		
		return new ResponseEntity<>(BaseCommon.error(BaseCommon.baseCode(status.value()),status.value()), headers, status);
	}

}