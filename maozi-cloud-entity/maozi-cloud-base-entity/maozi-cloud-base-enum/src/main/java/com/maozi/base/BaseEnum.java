package com.maozi.base;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 基础枚举接口
 * <p>
 * 统一所有枚举类型的行为，继承 MyBatis-Plus {@link IEnum} 接口，
 * 使枚举值能够自动与数据库整型字段相互转换。
 * </p>
 *
 * @author maozi
 */
public interface BaseEnum extends IEnum<Integer> {

	/**
	 * 获取枚举的整型值
	 *
	 * @return 枚举值
	 */
	Integer getValue();

	/**
	 * 获取枚举的中文描述
	 *
	 * @return 枚举描述
	 */
	String getDesc();

}
