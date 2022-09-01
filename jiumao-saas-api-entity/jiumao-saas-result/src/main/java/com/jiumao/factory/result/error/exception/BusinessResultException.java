package com.jiumao.factory.result.error.exception;

import static com.jiumao.factory.BaseResultFactory.error;

import com.jiumao.factory.result.code.CodeAttribute;
import com.jiumao.factory.result.error.ErrorResult;

import lombok.Data;

@Data
public class BusinessResultException extends RuntimeException {

    private static final long serialVersionUID = 3034121940056795549L;
    
    private ErrorResult errorResult;
    
    public BusinessResultException(String message) {
        super(message);
        this.errorResult=error(new CodeAttribute(message));
    }
    
    public BusinessResultException(String message,Integer httpCode) {
        super(message);
        this.errorResult=error(new CodeAttribute(message),httpCode);
    }
    
    public BusinessResultException(Integer code,String message) {
        super(message);
        this.errorResult=error(new CodeAttribute(code,message),code);
    }
    
    public BusinessResultException(Integer code,String message,Integer httpCode) {
        super(message);
        this.errorResult=error(new CodeAttribute(code,message),httpCode);
    }

    public BusinessResultException(CodeAttribute codeAttribute) {
        super(codeAttribute.getMessage());
        this.errorResult=error(codeAttribute);
    }
    
    public BusinessResultException(CodeAttribute codeAttribute,Integer httpCode) {
        super(codeAttribute.getMessage());
        this.errorResult=error(codeAttribute,httpCode);
    }
    
    public BusinessResultException(ErrorResult errorResult) {
        super(errorResult.getMessage());
        this.errorResult=errorResult;
    }
    
    public BusinessResultException(ErrorResult errorResult,Integer httpCode) {
        super(errorResult.getMessage());
        this.errorResult=errorResult;
    }
    
}