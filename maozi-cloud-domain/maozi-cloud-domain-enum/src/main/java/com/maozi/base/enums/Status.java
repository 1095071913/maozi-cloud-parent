package com.maozi.base.enums;

import com.maozi.common.enums.BaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 状态枚举
 * <p>
 * 用于标识数据记录的启用/禁用状态，是系统中通用的状态控制枚举。
 * 通常应用于用户账号状态、功能开关状态、配置项是否生效等场景。
 * </p>
 * <p>
 * 配合 Swagger 的 {@link Schema} 注解，在接口文档中以整型形式展示，
 * 方便前端和其他服务理解字段含义。
 * </p>
 *
 * @author maozi
 */
@Schema(description = "状态",type = "integer")
public enum Status implements BaseEnum {

	/** 禁用状态，值为 0 */
	DISABLE(0,"禁用"),

	/** 启用状态，值为 1 */
	ENABLE(1,"启用"),

	;

	/**
	 * 枚举构造方法
	 *
	 * @param value 枚举的整型值，对应数据库中存储的状态字段值
	 * @param desc  枚举的中文描述，用于展示和日志输出
	 */
	Status(Integer value,String desc) {

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
