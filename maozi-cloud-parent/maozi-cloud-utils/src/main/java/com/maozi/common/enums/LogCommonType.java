package com.maozi.common.enums;

import lombok.Getter;

/**
 * 日志通用类型枚举
 * <p>
 * 用于标识系统中不同入口的日志来源类型，覆盖了微服务架构中的主要通信和调用方式。
 * 通过该枚举可以在日志记录时快速区分请求的来源渠道，便于日志分类、检索和问题排查。
 * </p>
 * <p>
 * 支持的日志类型包括：
 * <ul>
 *     <li>GATEWAY - 网关层日志，记录通过 API 网关进入的请求</li>
 *     <li>WEB - Web 接口日志，记录 HTTP 接口的请求和响应</li>
 *     <li>RPC - RPC 调用日志，记录服务间远程过程调用的详细信息</li>
 *     <li>JOB - 定时任务日志，记录定时调度任务的执行情况</li>
 *     <li>MQ - 消息队列日志，记录消息的发送、接收和处理过程</li>
 *     <li>REST_TEMPLATE - RestTemplate 日志，记录三方 HTTP 请求调用</li>
 *     <li>WEB_SOCKET - WebSocket 日志，记录 WebSocket 长连接的通信信息</li>
 *     <li>RPC_RETRY - RPC 重试日志，记录服务间远程调用失败后的重试请求</li>
 * </ul>
 * </p>
 *
 * @author maozi
 */
public enum LogCommonType implements BaseEnum {

    /** 网关层日志，值为 0，记录通过 API 网关进入的请求 */
    GATEWAY(0,"Gateway"),

    /** Web 接口日志，值为 1，记录 HTTP 接口的请求和响应 */
    WEB(1,"Web"),

    /** RPC 调用日志，值为 2，记录服务间远程过程调用的详细信息 */
    RPC(2,"RPC"),

    /** 定时任务日志，值为 3，记录定时调度任务的执行情况 */
    JOB(3,"Job"),

    /** 消息队列日志，值为 4，记录消息的发送、接收和处理过程 */
    MQ(4,"MQ"),

    /** RestTemplate日志，值为 5，三方请求调用的详细信息 */
    REST_TEMPLATE(5,"RestTemplate"),

    /** WebSocket 日志，值为 6，记录 WebSocket 长连接的通信信息 */
    WEB_SOCKET(6,"WebSocket"),

    /** RPC 重试日志，值为 7，记录服务间远程调用失败后的重试请求 */
    RPC_RETRY(7,"RPCRetry"),

    ;

    /**
     * 枚举构造方法
     *
     * @param value 枚举的整型值，对应数据库中存储的日志类型字段
     * @param desc  日志类型的英文描述，与系统中各组件的命名保持一致
     */
    LogCommonType(Integer value,String desc) {

        this.value = value;

        this.desc = desc;

    }

    /** 枚举的整型值，对应数据库中存储的日志类型字段 */
    @Getter
    private final Integer value;

    /** 日志类型的英文描述 */
    @Getter
    private final String desc;

    /**
     * 输出枚举的字符串表示
     * <p>
     * 重写默认的 toString 方法，返回格式为 "值.描述" 的字符串，
     * 便于在日志和调试信息中快速识别日志来源类型。
     * </p>
     *
     * @return 格式为 "值.描述" 的字符串，例如 "0.Gateway"、"1.Web" 等
     */
    @Override
    public String toString() {
        return value + "." + desc;
    }

}
