package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;
import lombok.Setter;

public enum Status implements BaseEnum {
	
	DISABLE(0,"禁用"),

	ENABLE(1,"启用"),

	;
	
	Status(Integer value,String desc) {
		
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
