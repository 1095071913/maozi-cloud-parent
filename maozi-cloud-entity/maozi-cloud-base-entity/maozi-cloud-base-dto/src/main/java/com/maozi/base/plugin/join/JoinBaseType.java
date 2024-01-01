package com.maozi.base.plugin.join;

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
public enum JoinBaseType {

	innerJoin("innerJoin","内连接"),leftJoin("leftJoin","左连接"),rightJoin("rightJoin","右连接");

	JoinBaseType(String type,String name) {
		
		this.type = type;
		
		this.name = name;
		
	}
	
	private String type;
	
	private String name;
	
}
