package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;



public enum EnvironmentType implements BaseEnum {
	
	LOCAL(0,"local"),

	TEST(1,"test"),

	PROD(2,"prod"),

	;
	
	EnvironmentType(Integer value,String desc) {
		
		this.value = value;
		
		this.desc = desc;
		
	}
	
	@Getter
	private final Integer value;
	
	@Getter
	private final String desc;

	@Override
	public String toString() {
		return value + "." + desc;
	}

}
