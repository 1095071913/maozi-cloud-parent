package com.maozi.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 基础枚举接口
 * <p>
 * 本接口是项目中所有枚举类型的统一抽象层，继承了 MyBatis-Plus 的 {@link IEnum} 接口，
 * 使得实现此接口的枚举类型能够自动与数据库中的整型字段进行相互转换，
 * 无需手动编写类型处理器（TypeHandler）。
 * </p>
 * <p>
 * 所有业务枚举（如状态枚举、删除标记枚举等）都应实现此接口，
 * 以保证枚举值和描述信息的一致性访问方式。
 * </p>
 *
 * @author maozi
 * @see IEnum MyBatis-Plus 枚举接口
 */
public interface BaseEnum extends IEnum<Integer> {

	/**
	 * 获取枚举的中文描述
	 * <p>
	 * 用于在日志、接口文档、前端展示等场景中提供枚举值的人类可读说明。
	 * </p>
	 *
	 * @return 枚举的中文描述信息
	 */
	String getDesc();

}
