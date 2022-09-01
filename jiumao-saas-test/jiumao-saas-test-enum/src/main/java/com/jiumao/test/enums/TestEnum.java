package com.jiumao.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.jiumao.enums.config.annotation.SwaggerDisplayEnum;
import lombok.Getter;
import lombok.Setter;

@SwaggerDisplayEnum
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
