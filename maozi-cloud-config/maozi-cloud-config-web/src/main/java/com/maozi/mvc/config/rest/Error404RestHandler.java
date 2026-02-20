package com.maozi.mvc.config.rest;

import com.maozi.base.error.code.SystemErrorCode;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.error.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class Error404RestHandler extends BaseCommon {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResult<Void> noHandlerFoundException(NoHandlerFoundException e) {
        return BaseCommon.error(SystemErrorCode.NOT_RESOURCE_ERROR);
    }

}