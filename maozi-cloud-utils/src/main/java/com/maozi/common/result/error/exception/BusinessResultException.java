package com.maozi.common.result.error.exception;

import com.maozi.common.ResultUtil;
import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.code.ErrorCode;
import lombok.Data;

import java.io.Serial;

@Data
public class BusinessResultException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private ErrorResult<?> errorResult;

    public BusinessResultException(String message) {
        super(message);
        this.errorResult = ResultUtil.error(new ErrorCode(message));
    }
    
    public BusinessResultException(Integer code,String message) {
        super(message);
        this.errorResult = ResultUtil.error(new ErrorCode(code,message));
    }

    public <D> BusinessResultException(Integer code,String message,D data) {
        super(message);
        this.errorResult = ResultUtil.error(new ErrorCode(code,message),data);
    }

    public BusinessResultException(ErrorCode errorCode) {
        super(errorCode.getExceptionMessage());
        this.errorResult = ResultUtil.error(errorCode);
    }

    public <D> BusinessResultException(ErrorCode errorCode,D errorData) {
        super(errorCode.getExceptionMessage());
        this.errorResult = ResultUtil.error(errorCode,errorData);
    }

    public BusinessResultException(String exceptionMessage,ErrorCode errorCode) {
        super(exceptionMessage);
        this.errorResult = ResultUtil.error(errorCode).setExceptionMessage(exceptionMessage);
    }

    public <D> BusinessResultException(String exceptionMessage, ErrorCode errorCode, D errorData) {
        super(exceptionMessage);
        this.errorResult = ResultUtil.error(errorCode,errorData).setExceptionMessage(exceptionMessage);
    }

    public <T> BusinessResultException(ErrorResult<T> errorResult) {
        super(errorResult.getExceptionMessage());
        this.errorResult = errorResult;
    }

    public <T> BusinessResultException(String exceptionMessage,ErrorResult<T> errorResult) {
        super(exceptionMessage);
        this.errorResult = errorResult.setExceptionMessage(exceptionMessage);
    }

    public BusinessResultException setResource(String resource) {
        this.errorResult.setMessage(resource + this.errorResult.getMessage());
        return this;
    }

    public BusinessResultException setMessage(String message) {
        this.errorResult.setMessage(message);
        return this;
    }

    public BusinessResultException setMessage(String message,String exceptionMessage) {
        this.errorResult.setMessage(message,exceptionMessage);
        return this;
    }

    public BusinessResultException setHttpCode(Integer httpCode) {
        this.errorResult.setHttpCode(httpCode);
        return this;
    }
    
}