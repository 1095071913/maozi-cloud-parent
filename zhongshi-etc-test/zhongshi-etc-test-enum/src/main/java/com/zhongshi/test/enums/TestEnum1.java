package com.zhongshi.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zhongshi.factory.result.code.IBaseEnum;

import lombok.Getter;
import lombok.Setter;

public enum TestEnum1 implements IBaseEnum{

	test1(0,"test1"),test2(1,"test2");
	
	TestEnum1(Integer key,String value) {
		this.key=key;
		this.value=value;
	}
	
	@Getter
	@Setter 
	@EnumValue
	private Integer key;
	
	@Getter
	@Setter 
	@JsonValue
	public String value;
	
}
