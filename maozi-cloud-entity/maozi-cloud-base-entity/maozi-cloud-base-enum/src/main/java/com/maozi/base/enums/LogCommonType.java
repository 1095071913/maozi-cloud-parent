package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

/**
 * 日志通用类型枚举
 * <p>
 * 用于标识不同入口的日志类型，包括网关、Web 接口、RPC 调用、定时任务、消息队列、WebSocket 等。
 * </p>
 *
 * @author maozi
 */
public enum LogCommonType implements BaseEnum {

    /** 网关 */
    GATEWAY(0,"Gateway"),

    /** Web 接口 */
    WEB(1,"Web"),

    /** RPC 调用 */
    RPC(2,"Rpc"),

    /** 定时任务 */
    JOB(3,"Job"),

    /** 消息队列 */
    MQ(4,"MQ"),

    /** WebSocket */
    WEB_SOCKET(5,"WebSocket"),

    ;

    /** 构造方法 */
    LogCommonType(Integer value,String desc) {

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
