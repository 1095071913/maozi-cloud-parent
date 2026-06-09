package com.maozi.base.plugin.query;

import lombok.Getter;

/**
 * 查询基础类型枚举
 * <p>
 * 定义支持的查询条件类型，包括等于、模糊查询、范围查询、不等于、大于等于、小于等于。
 * </p>
 *
 * @author maozi
 */
@Getter
public enum QueryBaseType {

	/** 等于 */
	EQ("eq", "等于"),

	/** 模糊查询 */
	LIKE("like", "模糊查询"),

	/** 范围等值查询 */
	IN("in", "范围等值查询"),

	/** 不等于 */
	NE("ne", "不等于"),

	/** 大于等于 */
	GE("ge", "大于等于"),

	/** 小于等于 */
	LE("le","小于等于"),

	;

	/** 构造方法 */
	QueryBaseType(String type,String name) {

		this.type = type;

		this.name = name;

	}

	/** 查询类型标识 */
	private final String type;

	/** 查询类型名称 */
	private final String name;

}
