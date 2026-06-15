package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

/**
 * 删除状态枚举
 * <p>
 * 用于标识数据记录的逻辑删除状态。逻辑删除是一种软删除策略，
 * 并非真正从数据库中移除记录，而是通过标记字段来表示数据是否已被"删除"，
 * 这样可以在需要时进行数据恢复或审计追踪。
 * </p>
 * <p>
 * 通常与 MyBatis-Plus 的逻辑删除插件配合使用，
 * 自动在查询条件中追加 deleted = 0 的过滤条件。
 * </p>
 *
 * @author maozi
 */
public enum Deleted implements BaseEnum {

	/** 未删除状态，值为 0，表示数据正常存在 */
	NONE(0,"未删除"),

	/** 已删除状态，值为 1，表示数据已被逻辑删除 */
	DEL(1,"已删除"),

	;

	/**
	 * 枚举构造方法
	 *
	 * @param value 枚举的整型值，对应数据库中存储的删除标记字段值
	 * @param desc  枚举的中文描述，用于展示和日志输出
	 */
	Deleted(Integer value,String desc) {

		this.value = value;

		this.desc = desc;

	}

	/** 枚举的整型值，对应数据库中存储的删除标记字段 */
	@Getter
	private final Integer value;

	/** 枚举的中文描述，用于展示和日志输出 */
	@Getter
	private final String desc;

	/**
	 * 输出枚举的字符串表示
	 * <p>
	 * 重写默认的 toString 方法，返回格式为 "值.描述" 的字符串，
	 * 便于在日志和调试信息中快速识别删除状态。
	 * </p>
	 *
	 * @return 格式为 "值.描述" 的字符串，例如 "0.未删除" 或 "1.已删除"
	 */
	@Override
	public String toString() {
		return value + "." + desc;
	}


}
