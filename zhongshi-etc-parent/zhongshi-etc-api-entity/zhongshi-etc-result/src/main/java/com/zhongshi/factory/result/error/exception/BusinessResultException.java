package com.zhongshi.factory.result.error.exception;

import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.error.ErrorResult;
import lombok.Data;
import static com.zhongshi.factory.BaseResultFactory.error;

@Data
public class BusinessResultException extends RuntimeException {

    private static final long serialVersionUID = 3034121940056795549L;
    
    private ErrorResult errorResult;

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