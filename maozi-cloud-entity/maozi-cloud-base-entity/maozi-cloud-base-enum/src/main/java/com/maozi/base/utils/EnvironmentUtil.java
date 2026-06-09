package com.maozi.base.utils;

import com.maozi.base.enums.EnvironmentType;
import com.maozi.common.context.ApplicationEnvironmentContext;

/**
 * 环境工具类
 * <p>
 * 提供判断当前运行环境类型的便捷方法，基于 {@link ApplicationEnvironmentContext} 中存储的环境标识。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/4/26 09:27
 */
public class EnvironmentUtil {

    /**
     * 判断当前环境是否为指定类型
     *
     * @param type 目标环境类型
     * @return 当前环境与指定类型一致时返回 true
     */
    public static Boolean isEnvironment(EnvironmentType type) {
        return type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
    }

    /**
     * 判断当前环境是否不为指定类型
     *
     * @param type 目标环境类型
     * @return 当前环境与指定类型不一致时返回 true
     */
    public static Boolean notEnvironment(EnvironmentType type) {
        return !type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
    }

}
