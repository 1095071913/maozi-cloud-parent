package com.maozi.mvc.config.code;

import cn.hutool.extra.spring.SpringUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.result.error.code.AbstractBaseErrorCode;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@Component
public class CodeConfig {

    public final static Map<Integer, ErrorCode> ERROR_CODES = new HashMap<>();

    public CodeConfig(){
        initErrorCode();
    }

    private void initErrorCode(){

        Map<String, AbstractBaseErrorCode> codeBeans = SpringUtil.getBeansOfType(AbstractBaseErrorCode.class);

        for(AbstractBaseErrorCode errorCodeBean : codeBeans.values()){

            try {

                for(Field field : errorCodeBean.getClass().getDeclaredFields()){

                    field.setAccessible(true);

                    if(field.get(errorCodeBean) instanceof ErrorCode errorCode){
                        ERROR_CODES.put(errorCode.getCode(),errorCode);
                    }

                }

            } catch (Exception e) {
                LogUtil.error(log,e);
            }

        }

    }

    public static ErrorCode getErrorCode(Integer code) {

        ErrorCode errorCode = ERROR_CODES.get(code);

        return ObjectUtil.isNullEmpty(errorCode) ? SystemErrorCode.NOT_EXIST_CODE_ERROR : errorCode;

    }
}