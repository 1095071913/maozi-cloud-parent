package com.maozi.common;

import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author pengjinlong
 * @date 2026/4/29 10:13
 */
public class ObjectUtil {

    public static Boolean equals(Object a, Object b) {
        return Objects.equals(a,b);
    }

    public static boolean isNotNullEmpty(Object data){
        return !isNullEmpty(data);
    }

    public static boolean isNullEmpty(Object data){

        if(Objects.isNull(data)){
            return true;
        }

        if(data instanceof String string && StringUtils.isBlank(string)){
            return true;
        }

        if(data instanceof List<?> collection && CollectionUtil.isEmpty(collection)){
            return true;
        }

        return data instanceof Map<?, ?> map && CollectionUtil.isEmpty(map);

    }

    public static void isNullEmptyThrowError(Object data, String sourceName) {
        isNullEmptyThrowError(data,SystemErrorCode.DATA_NOT_EXIST_ERROR,sourceName);
    }

    public static void isNotNullEmptyThrowError(Object data, String sourceName) {
        isNotNullEmptyThrowError(data, SystemErrorCode.DATA_EXIST_ERROR, sourceName);
    }

    public static void isNullEmptyThrowError(Object data, ErrorCode errorCode, String resourceName) {
        if(isNullEmpty(data)) {
            throw new BusinessResultException(errorCode)
                    .setResource(resourceName);
        }
    }

    public static void isNotNullEmptyThrowError(Object data, ErrorCode errorCode, String resourceName) {
        if(isNotNullEmpty(data)) {
            throw new BusinessResultException(errorCode)
                    .setResource(resourceName);
        }
    }

    public static void isNullEmptyThrowError(Object data, ErrorCode errorCode) {
        if(isNullEmpty(data)) {
            throw new BusinessResultException(errorCode);
        }
    }

    public static void isNotNullEmptyThrowError(Object data, ErrorCode errorCode) {
        if(isNotNullEmpty(data)) {
            throw new BusinessResultException(errorCode);
        }
    }

    public static void checkConditionThrowError(boolean condition, String message) {
        if(condition) {
            return;
        }
        throw new BusinessResultException(message,SystemErrorCode.SYSTEM_ERROR)
                .setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);
    }

    public static void checkConditionThrowError(boolean condition, Integer code, String message) {
        if(condition) {
            return;
        }
        throw new BusinessResultException(code,message);
    }

    public static void checkConditionThrowError(boolean condition, ErrorCode errorCode, String message) {
        if(condition) {
            return;
        }
        throw new BusinessResultException(errorCode).setMessage(message);
    }

}
