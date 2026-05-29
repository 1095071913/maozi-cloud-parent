package com.maozi.base.utils;

import com.maozi.base.BaseEnum;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;

/**
 * @author pengjinlong
 * @date 2026/4/26 09:04
 */
public class EnumUtil {

    public static <T extends BaseEnum> T getEnum(Integer value, Class<T> clazz) {

        T[] values = clazz.getEnumConstants();

        for(T iEnum : values) {

            if(iEnum.getValue().intValue() == value.intValue()) {
                return iEnum;
            }

        }

        return null;

    }

    public static <T extends BaseEnum> T getEnumNullThrow(Integer value,Class<T> clazz,String resourceName) {

        T[] values = clazz.getEnumConstants();

        for(T iEnum : values) {

            if(iEnum.getValue().intValue() == value.intValue()) {
                return iEnum;
            }

        }

        throw new BusinessResultException(SystemErrorCode.DATA_NOT_EXIST_ERROR).setResource(resourceName);

    }

}
