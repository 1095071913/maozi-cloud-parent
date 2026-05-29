package com.maozi.common;

import com.maozi.common.result.error.ErrorResult;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.success.SuccessResult;

/**
 * @author pengjinlong
 * @date 2026/4/26 08:18
 */
public class ResultUtil {

    //默认成功结果集
    public final static SuccessResult<Void> DEFAULT_SUCCESS = new SuccessResult<>(null);

    public static SuccessResult<Void> success() {
        return DEFAULT_SUCCESS;
    }

    public static <T> SuccessResult<T> success(T data) {
        return new SuccessResult<>(data);
    }

    public static <T> ErrorResult<T> error(ErrorCode errorCode) {
        return new ErrorResult<>(errorCode);
    }

    public static <T> ErrorResult<T> error(ErrorCode errorCode,T errorData) {
        return new ErrorResult<>(errorCode,errorData);
    }

}
