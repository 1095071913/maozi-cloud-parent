package com.maozi.system.redirect.enums;

import com.maozi.common.enums.BaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author pengjinlong
 * @since 2026/8/14 13:06
 */
@Schema(description = "重定向类型",type = "integer")
public enum RedirectType implements BaseEnum {

    LOGIN(0,"登陆页"),

    ;

    /**
     * 枚举构造方法
     *
     * @param value 枚举的整型值，对应数据库中存储的状态字段值
     * @param desc  枚举的中文描述，用于展示和日志输出
     */
    RedirectType(Integer value,String desc) {

        this.value = value;

        this.desc = desc;

    }

    /** 枚举的整型值，对应数据库中存储的状态字段 */
    @Getter
    private final Integer value;

    /** 枚举的中文描述，用于展示和日志输出 */
    @Getter
    private final String desc;

    /**
     * 输出枚举的字符串表示
     * <p>
     * 重写默认的 toString 方法，返回格式为 "值.描述" 的字符串，
     * 便于在日志和调试信息中快速识别枚举的含义。
     * </p>
     *
     * @return 格式为 "值.描述" 的字符串，例如 "0.禁用" 或 "1.启用"
     */
    @Override
    public String toString() {
        return value + "." + desc;
    }

}
