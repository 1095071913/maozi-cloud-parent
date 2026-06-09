package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

/**
 * 删除状态枚举
 * <p>
 * 用于标识数据记录的逻辑删除状态。
 * </p>
 *
 * @author maozi
 */
public enum Deleted implements BaseEnum {

	/** 未删除 */
	NONE(0,"未删除"),

	/** 已删除 */
	DEL(1,"已删除"),

	;

	/** 构造方法 */
	Deleted(Integer value,String desc) {

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
