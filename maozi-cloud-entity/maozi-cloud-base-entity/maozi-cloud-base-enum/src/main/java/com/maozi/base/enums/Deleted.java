package com.maozi.base.enums;

import com.maozi.base.BaseEnum;
import lombok.Getter;

public enum Deleted implements BaseEnum {
	
	NONE(0,"未删除"),

	DEL(1,"已删除"),

	;
	
	Deleted(Integer value,String desc) {
		
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