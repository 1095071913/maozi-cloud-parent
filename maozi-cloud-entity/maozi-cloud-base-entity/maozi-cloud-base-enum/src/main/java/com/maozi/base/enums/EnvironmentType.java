package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

/**
 * 环境类型枚举
 * <p>
 * 用于区分当前应用运行的环境：本地开发、测试、生产。
 * </p>
 *
 * @author maozi
 */
public enum EnvironmentType implements BaseEnum {

	/** 本地开发环境 */
	LOCAL(0,"local"),

	/** 测试环境 */
	TEST(1,"test"),

	/** 生产环境 */
	PROD(2,"prod"),

	;

	/** 构造方法 */
	EnvironmentType(Integer value,String desc) {

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
