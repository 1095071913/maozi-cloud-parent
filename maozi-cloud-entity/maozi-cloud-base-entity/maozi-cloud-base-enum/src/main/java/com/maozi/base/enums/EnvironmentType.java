package com.maozi.base.enums;

import com.maozi.base.IEnum;
import lombok.Getter;
import lombok.Setter;

/**	
 * 
 *  Specifications：功能
 * 
 *  Author：彭晋龙
 * 
 *  Creation Date：2021-12-18:16:32:34
 *
 *  Copyright Ownership：xiao mao zi
 * 
 *  Agreement That：Apache 2.0
 * 
 */

public enum EnvironmentType implements IEnum {
	
	localhost(0,"localhost"),test(1,"test"),production(2,"production");
	
	EnvironmentType(Integer value,String desc) {
		
		this.value = value;
		
		this.desc = desc;
		
	}
	
	@Getter
	@Setter
	private Integer value;
	
	@Getter
	@Setter 
	private String desc;

	@Override
	public String toString() {
		return value+"."+desc;
	}

}
