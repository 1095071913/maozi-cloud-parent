package com.maozi.base.utils;

import com.maozi.base.enums.EnvironmentType;
import com.maozi.common.context.ApplicationEnvironmentContext;

/**
 * @author pengjinlong
 * @date 2026/4/26 09:27
 */
public class EnvironmentUtil {

    public static Boolean isEnvironment(EnvironmentType type) {
        return type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
    }

    public static Boolean notEnvironment(EnvironmentType type) {
        return !type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
    }

}
