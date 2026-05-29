package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

public enum Status implements BaseEnum {
	
	DISABLE(0,"禁用"),

	ENABLE(1,"启用"),

	;
	
	Status(Integer value,String desc) {
		
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
