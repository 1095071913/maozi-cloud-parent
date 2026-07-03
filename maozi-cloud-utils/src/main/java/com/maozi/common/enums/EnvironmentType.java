package com.maozi.common.enums;

import lombok.Getter;

/**
 * 环境类型枚举
 * <p>
 * 用于区分当前应用运行的环境类型，支持以下三种环境：
 * <ul>
 *     <li>LOCAL - 本地开发环境，供开发人员在本地进行开发和调试</li>
 *     <li>TEST - 测试环境，用于集成测试和功能验证</li>
 *     <li>PROD - 生产环境，面向最终用户的正式运行环境</li>
 * </ul>
 * </p>
 * <p>
 * 可以在代码中便捷地判断当前运行环境，从而实现不同环境下的差异化逻辑处理，
 * 例如：仅在测试环境输出详细日志、仅在本地环境启用调试接口等。
 * </p>
 *
 * @author maozi
 */
public enum EnvironmentType implements BaseEnum {

	/** 本地开发环境，值为 0，描述为 "local" */
	LOCAL(0,"local"),

	/** 测试环境，值为 1，描述为 "test" */
	TEST(1,"test"),

	/** 生产环境，值为 2，描述为 "prod" */
	PROD(2,"prod"),

	;

	/**
	 * 枚举构造方法
	 *
	 * @param value 枚举的整型值，用于数据库存储和环境标识
	 * @param desc  环境描述字符串，与配置文件中的环境标识（如 spring.profiles.active）保持一致
	 */
	EnvironmentType(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

	}

	/** 枚举的整型值，用于数据库存储和环境标识 */
	@Getter
	private final Integer value;

	/** 环境描述字符串，与配置文件中的环境标识保持一致 */
	@Getter
	private final String desc;

	/**
	 * 输出枚举的字符串表示
	 * <p>
	 * 重写默认的 toString 方法，返回格式为 "值.描述" 的字符串，
	 * 便于在日志和调试信息中快速识别当前环境类型。
	 * </p>
	 *
	 * @return 格式为 "值.描述" 的字符串，例如 "0.local"、"1.test" 或 "2.prod"
	 */
	@Override
	public String toString() {
		return value + "." + desc;
	}

}
