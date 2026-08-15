package com.maozi.common;

import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 对象工具类
 * <p>
 * 提供对象空值判断、相等比较、条件校验等通用方法。
 * 支持多种数据类型的空值判断：CharSequence、Array、Collection、Optional、Map。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/4/29 10:13
 */
public class ObjectUtil {

    /**
     * 判断两个对象是否相等
     *
     * @param a 对象 a
     * @param b 对象 b
     * @return 相等返回 true
     */
    public static Boolean equals(Object a, Object b) {
        return Objects.equals(a,b);
    }

    /**
     * 判断对象是否不为空
     *
     * @param data 待检查对象
     * @return 不为空返回 true
     */
    public static boolean isNotNullEmpty(Object data){
        return !isNullEmpty(data);
    }

    /**
     * 判断对象是否为空
     * <p>
     * 支持 CharSequence、Array、Collection、Optional、Map 类型的空值判断。
     * 其他类型对象仅判断是否为 null。
     * </p>
     *
     * @param data 待检查对象
     * @return 为空返回 true
     */
    public static boolean isNullEmpty(Object data){

        if(data == null){
            return true;
        }

        if (data instanceof CharSequence charSequence) {
            return charSequence.isEmpty();
        }

        if(data.getClass().isArray()){
            return Array.getLength(data) == 0;
        }

        if(data instanceof Collection<?> collection){
            return collection.isEmpty();
        }

        if(data instanceof Optional<?> optional){
            return optional.isEmpty();
        }

        return data instanceof Map<?, ?> map && map.isEmpty();

    }

    /**
     * 对象为空时抛出业务异常（默认数据不存在错误码）
     *
     * @param data 待检查对象
     * @param sourceName 资源名称
     */
    public static void isNullEmptyThrowError(Object data, String sourceName) {
        isNullEmptyThrowError(data,SystemErrorCode.DATA_NOT_EXIST_ERROR,sourceName);
    }

    /**
     * 对象不为空时抛出业务异常（默认数据已存在错误码）
     *
     * @param data 待检查对象
     * @param sourceName 资源名称
     */
    public static void isNotNullEmptyThrowError(Object data, String sourceName) {
        isNotNullEmptyThrowError(data, SystemErrorCode.DATA_EXIST_ERROR, sourceName);
    }

    /**
     * 对象为空时抛出业务异常
     *
     * @param data 待检查对象
     * @param errorCode 错误码
     * @param resourceName 资源名称
     */
    public static void isNullEmptyThrowError(Object data, ErrorCode errorCode, String resourceName) {
        if(isNullEmpty(data)) {
            throw new BusinessResultException(errorCode)
                    .setResource(resourceName);
        }
    }

    /**
     * 对象不为空时抛出业务异常
     *
     * @param data 待检查对象
     * @param errorCode 错误码
     * @param resourceName 资源名称
     */
    public static void isNotNullEmptyThrowError(Object data, ErrorCode errorCode, String resourceName) {
        if(isNotNullEmpty(data)) {
            throw new BusinessResultException(errorCode)
                    .setResource(resourceName);
        }
    }

    /**
     * 对象为空时抛出业务异常（无资源名称）
     *
     * @param data 待检查对象
     * @param errorCode 错误码
     */
    public static void isNullEmptyThrowError(Object data, ErrorCode errorCode) {
        if(isNullEmpty(data)) {
            throw new BusinessResultException(errorCode);
        }
    }

    /**
     * 对象不为空时抛出业务异常（无资源名称）
     *
     * @param data 待检查对象
     * @param errorCode 错误码
     */
    public static void isNotNullEmptyThrowError(Object data, ErrorCode errorCode) {
        if(isNotNullEmpty(data)) {
            throw new BusinessResultException(errorCode);
        }
    }

    /**
     * 条件校验，条件不满足时抛出系统异常
     *
     * @param condition 校验条件
     * @param message 异常信息
     */
    public static void checkConditionThrowError(boolean condition, String message) {
        if(condition) {
            return;
        }
        throw new BusinessResultException(message,SystemErrorCode.SYSTEM_ERROR)
                .setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);
    }

    /**
     * 条件校验，条件不满足时抛出指定编码和信息的异常
     *
     * @param condition 校验条件
     * @param code 异常编码
     * @param message 异常信息
     */
    public static void checkConditionThrowError(boolean condition, Integer code, String message) {
        if(condition) {
            return;
        }
        throw new BusinessResultException(code,message);
    }

    /**
     * 条件校验，条件不满足时抛出指定错误码的异常
     *
     * @param condition 校验条件
     * @param errorCode 错误码
     * @param message 异常信息
     */
    public static void checkConditionThrowError(boolean condition, ErrorCode errorCode, String message) {
        if(condition) {
            return;
        }
        throw new BusinessResultException(errorCode).setMessage(message);
    }

}
