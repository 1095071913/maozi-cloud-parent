package com.maozi.base.utils;

import com.maozi.base.BaseEnum;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;

/**
 * 枚举工具类
 * <p>
 * 提供根据整型值查找对应枚举实例的通用方法。所有实现了 {@link BaseEnum} 接口的枚举类型
 * 都可以使用本工具类进行值到枚举实例的转换，避免在每个枚举类中重复编写查找逻辑。
 * </p>
 * <p>
 * 提供两种查找策略：
 * <ul>
 *     <li>{@link #getEnum(Integer, Class)} - 静默查找，未找到时返回 null</li>
 *     <li>{@link #getEnumNullThrow(Integer, Class, String)} - 严格查找，未找到时抛出业务异常</li>
 * </ul>
 * </p>
 *
 * @author pengjinlong
 * @since 2026/4/26 09:04
 */
public class EnumUtil {

    /**
     * 根据整型值获取对应的枚举实例（静默模式）
     * <p>
     * 遍历指定枚举类型的所有常量，逐一比较其值与传入值是否相等，
     * 找到匹配项则返回对应的枚举实例。如果遍历完所有常量仍未找到匹配项，
     * 则返回 null，不会抛出异常。
     * </p>
     *
     * @param value 要查找的枚举整型值
     * @param clazz 枚举类型的 Class 对象，用于获取该枚举的所有常量
     * @param <T>   枚举类型泛型，必须实现 {@link BaseEnum} 接口
     * @return 匹配的枚举实例，未找到时返回 null
     */
    public static <T extends BaseEnum> T getEnum(Integer value, Class<T> clazz) {

        // 通过反射获取该枚举类型的所有常量数组
        T[] values = clazz.getEnumConstants();

        // 遍历所有枚举常量，逐一比较整型值
        for(T iEnum : values) {

            // 使用 intValue() 比较，避免 Integer 对象的引用比较陷阱
            if(iEnum.getValue().intValue() == value.intValue()) {
                return iEnum;
            }

        }

        // 未找到匹配的枚举常量，返回 null
        return null;

    }

    /**
     * 根据整型值获取对应的枚举实例（严格模式）
     * <p>
     * 遍历指定枚举类型的所有常量，逐一比较其值与传入值是否相等，
     * 找到匹配项则返回对应的枚举实例。如果遍历完所有常量仍未找到匹配项，
     * 则抛出 {@link BusinessResultException} 业务异常，携带数据不存在的错误码和资源名称。
     * </p>
     * <p>
     * 适用于业务上要求枚举值必须有效的场景，例如前端传入的状态值必须在合法范围内。
     * </p>
     *
     * @param value         要查找的枚举整型值
     * @param clazz         枚举类型的 Class 对象，用于获取该枚举的所有常量
     * @param resourceName  资源名称，用于异常提示信息中标识哪个资源的数据不存在
     * @param <T>           枚举类型泛型，必须实现 {@link BaseEnum} 接口
     * @return 匹配的枚举实例
     * @throws BusinessResultException 未找到对应枚举时抛出，错误码为 {@link SystemErrorCode#DATA_NOT_EXIST_ERROR}
     */
    public static <T extends BaseEnum> T getEnumNullThrow(Integer value,Class<T> clazz,String resourceName) {

        // 通过反射获取该枚举类型的所有常量数组
        T[] values = clazz.getEnumConstants();

        // 遍历所有枚举常量，逐一比较整型值
        for(T iEnum : values) {

            // 使用 intValue() 比较，避免 Integer 对象的引用比较陷阱
            if(iEnum.getValue().intValue() == value.intValue()) {
                return iEnum;
            }

        }

        // 未找到匹配的枚举常量，抛出业务异常，附带资源名称用于错误提示
        throw new BusinessResultException(SystemErrorCode.DATA_NOT_EXIST_ERROR).setResource(resourceName);

    }

}
