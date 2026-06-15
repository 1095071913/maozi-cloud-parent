package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

/**
 * 存储类型枚举
 * <p>
 * 用于标识数据的存储介质类型，当前支持数据库存储。
 * 后续可根据业务需要扩展其他存储类型，如 Redis 缓存、Elasticsearch 搜索引擎、
 * OSS 对象存储等。通过该枚举可以统一管理系统中使用的各类存储介质。
 * </p>
 *
 * @author maozi
 */
public enum StoreClassType implements BaseEnum {

    /** 数据库存储，值为 0，表示数据存储在关系型数据库中 */
    DB(0,"数据库");

    /**
     * 枚举构造方法
     *
     * @param value 枚举的整型值，对应数据库中存储的类型标识字段
     * @param desc  存储类型的中文描述，用于展示和日志输出
     */
    StoreClassType(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

    }

    /** 枚举的整型值，对应数据库中存储的类型标识字段 */
    @Getter
    private final Integer value;

    /** 存储类型的中文描述 */
    @Getter
    private final String desc;

    /**
     * 输出枚举的字符串表示
     * <p>
     * 重写默认的 toString 方法，返回格式为 "值.描述" 的字符串，
     * 便于在日志和调试信息中快速识别存储类型。
     * </p>
     *
     * @return 格式为 "值.描述" 的字符串，例如 "0.数据库"
     */
    @Override
    public String toString() {
        return value + "." + desc;
    }

}
