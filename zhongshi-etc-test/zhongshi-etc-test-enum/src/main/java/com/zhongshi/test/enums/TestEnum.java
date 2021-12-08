package com.zhongshi.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

public enum TestEnum {

	test1(0,"test1"),test2(1,"test2");
	
	TestEnum(Integer key,String value) {
		this.key=key;
		this.value=value;
	}
	
	@Getter
	@Setter 
	@EnumValue
	private Integer key;
	
	@Getter
	@Setter 
	public String value;
	
}
