package com.maozi.base.utils;

import com.maozi.base.enums.EnvironmentType;
import com.maozi.common.context.ApplicationEnvironmentContext;

/**
 * 环境工具类
 * <p>
 * 提供判断当前应用运行环境类型的便捷方法。基于 {@link ApplicationEnvironmentContext} 中
 * 存储的环境标识字符串（如 "local"、"test"、"prod"），与 {@link EnvironmentType} 枚举的
 * 描述值进行比对，从而判断当前环境是否为指定类型。
 * </p>
 * <p>
 * 典型使用场景：
 * <ul>
 *     <li>仅在开发环境输出详细日志或启用调试接口</li>
 *     <li>在非生产环境下执行测试数据的初始化</li>
 *     <li>在生产环境下禁用某些敏感操作或功能</li>
 *     <li>根据不同环境加载不同的配置策略</li>
 * </ul>
 * </p>
 *
 * @author pengjinlong
 * @since 2026/4/26 09:27
 * @see EnvironmentType 环境类型枚举
 * @see ApplicationEnvironmentContext 应用环境上下文
 */
public class EnvironmentUtil {

    /**
     * 判断当前环境是否为指定类型
     * <p>
     * 通过比较枚举类型的描述字符串（如 "local"、"test"、"prod"）与
     * {@link ApplicationEnvironmentContext#ENVIRONMENT} 中存储的当前环境标识来判断。
     * </p>
     *
     * @param type 目标环境类型，{@link EnvironmentType} 枚举值
     * @return 当前环境与指定类型一致时返回 true，否则返回 false
     */
    public static Boolean isEnvironment(EnvironmentType type) {
        // 将枚举的描述值与环境上下文中存储的环境标识进行字符串比较
        return type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
    }

    /**
     * 判断当前环境是否不为指定类型
     * <p>
     * 是 {@link #isEnvironment(EnvironmentType)} 的反向判断，
     * 用于快速排除特定环境的逻辑处理。
     * </p>
     *
     * @param type 目标环境类型，{@link EnvironmentType} 枚举值
     * @return 当前环境与指定类型不一致时返回 true，一致时返回 false
     */
    public static Boolean notEnvironment(EnvironmentType type) {
        // 对 isEnvironment 取反，判断当前环境是否不是指定类型
        return !type.getDesc().equals(ApplicationEnvironmentContext.ENVIRONMENT);
    }

}
