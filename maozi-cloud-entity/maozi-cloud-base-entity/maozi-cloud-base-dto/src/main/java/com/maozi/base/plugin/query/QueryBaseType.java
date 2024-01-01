package com.maozi.base.plugin.query;

import lombok.Getter;

/**  
 * @Title: QueryType.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-06-21 09:48:40
 *
 */

@Getter
public enum QueryBaseType {

	eq("eq","等于"),like("like","模糊查询"),in("in","范围等值查询"),ne("ne","不等于"),ge("ge","大于等于"),le("le","小于等于");
	
	QueryBaseType(String type,String name) {
		
		this.type = type;
		
		this.name = name;
		
	}
	
	private String type;
	
	private String name;
	
}
