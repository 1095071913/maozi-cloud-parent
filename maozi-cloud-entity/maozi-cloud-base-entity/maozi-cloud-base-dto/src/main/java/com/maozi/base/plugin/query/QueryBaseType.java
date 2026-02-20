package com.maozi.base.plugin.query;

import lombok.Getter;

@Getter
public enum QueryBaseType {

	EQ("eq","等于"),

	LIKE("like","模糊查询"),

	IN("in","范围等值查询"),

	NE("ne","不等于"),

	GE("ge","大于等于"),

	LE("le","小于等于"),

	;
	
	QueryBaseType(String type,String name) {
		
		this.type = type;
		
		this.name = name;
		
	}
	
	private final String type;
	
	private final String name;
	
}
