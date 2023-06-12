package com.maozi.base;

public interface IEnum extends com.baomidou.mybatisplus.annotation.IEnum<Integer> {

	Integer getValue();
	
	String getDesc();

}
