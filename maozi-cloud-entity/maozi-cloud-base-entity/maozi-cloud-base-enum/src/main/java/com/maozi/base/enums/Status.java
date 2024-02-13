package com.maozi.base.enums;

import com.maozi.base.IEnum;
import lombok.Getter;
import lombok.Setter;

/**  
 * @Title: Status.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-03-22 11:21:04
 *
 */
public enum Status implements IEnum {
	
	disable(0,"禁用"),enable(1,"启用");
	
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
