package com.maozi.base.plugin.join;

import lombok.Getter;

@Getter
public enum JoinBaseType {

	INNER_JOIN("innerJoin", "内连接"),

	LEFT_JOIN("leftJoin", "左连接"),

	RIGHT_JOIN("rightJoin", "右连接"),

	;

	JoinBaseType(String type,String name) {
		
		this.type = type;
		
		this.name = name;
		
	}

	@Getter
	private final String type;

	@Getter
	private final String name;
	
}
