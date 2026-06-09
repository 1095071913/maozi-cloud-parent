package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 状态枚举
 * <p>
 * 用于标识数据记录的启用/禁用状态。
 * </p>
 *
 * @author maozi
 */
@Schema(description = "状态",type = "integer")
public enum Status implements BaseEnum {

	/** 禁用 */
	DISABLE(0,"禁用"),

	/** 启用 */
	ENABLE(1,"启用"),

	;

	/** 构造方法 */
	Status(Integer value,String desc) {

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
