package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

/**
 * 存储类型枚举
 * <p>
 * 用于标识数据的存储介质类型。
 * </p>
 *
 * @author maozi
 */
public enum StoreClassType implements BaseEnum {

    /** 数据库 */
    DB(0,"数据库");

    /** 构造方法 */
    StoreClassType(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

    }

    /** 枚举值 */
    @Getter
    private final Integer value;

    /** 枚举描述 */
    @Getter
    private final String desc;

    /**
     * 输出枚举的字符串表示
     *
     * @return 格式为 "值.描述" 的字符串
     */
    @Override
    public String toString() {
        return value + "." + desc;
    }

}
