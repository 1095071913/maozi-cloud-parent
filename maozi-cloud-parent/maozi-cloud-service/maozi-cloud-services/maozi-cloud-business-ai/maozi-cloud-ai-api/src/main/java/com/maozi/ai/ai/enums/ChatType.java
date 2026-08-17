package com.maozi.ai.ai.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author pengjinlong
 * @since 2026/8/16 18:45
 */
@Schema(description = "AI对话类型",type = "integer")
public enum ChatType {

    FINISH(0,"完成对话"),

    OUTPUT(1,"持续对话中"),

    ;

    /**
     * 构造方法
     *
     * @param value 权限类型的数值编码
     * @param desc  权限类型的中文描述
     */
    ChatType(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

    }

    /** 权限类型的数值编码 */
    @Getter
    private final Integer value;

    /** 权限类型的中文描述 */
    @Getter
    private final String desc;

    /**
     * 重写toString方法，返回 "编码.描述" 格式的字符串
     *
     * @return 格式化的枚举字符串表示
     */
    @Override
    public String toString() {
        return value + "." + desc;
    }

}
