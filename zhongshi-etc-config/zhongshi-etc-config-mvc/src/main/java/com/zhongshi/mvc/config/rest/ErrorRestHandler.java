package com.zhongshi.mvc.config.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.zhongshi.factory.BaseResultFactory;

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
	
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}
		return new ResponseEntity<>(BaseResultFactory.error(BaseResultFactory.baseCode(status.value()),status.value()), headers, status);
	}

}