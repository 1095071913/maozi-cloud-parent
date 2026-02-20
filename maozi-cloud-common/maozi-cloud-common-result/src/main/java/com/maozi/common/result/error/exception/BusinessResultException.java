package com.maozi.common.result.error.exception;

import com.maozi.base.CodeData;
import com.maozi.common.result.error.ErrorResult;
import lombok.Data;

import java.io.Serial;

import static com.maozi.common.BaseCommon.error;

@Data
public class BusinessResultException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3034121940056795549L;
    
    private ErrorResult<?> errorResult;
    
    public BusinessResultException(String message) {
        super(message);
        this.errorResult = error(new CodeData<>(message));
    }
    
    public <T> BusinessResultException(String message,Integer httpCode) {
        super(message);
        this.errorResult = error(new CodeData<T>(message),httpCode);
    }
    
    public <T> BusinessResultException(Integer code,String message) {
        super(message);
        this.errorResult = error(new CodeData<T>(code,message),code);
    }
    
    public <T> BusinessResultException(Integer code,String message,Integer httpCode) {
        super(message);
        this.errorResult = error(new CodeData<T>(code,message),httpCode);
    }

    public <T> BusinessResultException(CodeData<T> codeData) {
        super(codeData.getMessage());
        this.errorResult = error(codeData);
    }

    public <T> BusinessResultException(String serviceName,CodeData<T> codeData) {
        super(serviceName + codeData.getMessage());
        this.errorResult = error(serviceName,codeData);
    }

    public <T> BusinessResultException(String serviceName,CodeData<T> codeData,Integer httpCode) {
        super(serviceName + codeData.getMessage());
        this.errorResult = error(serviceName,codeData);
    }
    
    public <T> BusinessResultException(CodeData<T> codeData,Integer httpCode) {
        super(codeData.getMessage());
        this.errorResult = error(codeData,httpCode);
    }

    public <D,T> BusinessResultException(CodeData<T> codeData,D errorData) {
        super(codeData.getMessage());
        CodeData<D> newCodedata = new CodeData<>(codeData.getCode(), codeData.getMessage(), errorData);
        this.errorResult = error(newCodedata);
    }

    public <D,T> BusinessResultException(CodeData<T> codeData,D errorData,Integer httpCode) {
        super(codeData.getMessage());
        CodeData<D> newCodedata = new CodeData<>(codeData.getCode(), codeData.getMessage(), errorData);
        this.errorResult = error(newCodedata,httpCode);
    }
    
    public <T> BusinessResultException(ErrorResult<T> errorResult) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
    
    public <T> BusinessResultException(ErrorResult<T> errorResult,Integer httpCode) {
        super(errorResult.getMessage());
        this.errorResult = errorResult;
    }
    
}