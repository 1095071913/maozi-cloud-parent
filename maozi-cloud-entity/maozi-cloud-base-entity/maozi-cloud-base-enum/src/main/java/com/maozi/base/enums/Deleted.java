package com.maozi.base.enums;
import com.maozi.base.IEnum;
import com.maozi.base.enums.config.annotation.SwaggerDisplayEnum;

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
@SwaggerDisplayEnum
public enum Deleted implements IEnum {
	
	none(0,"未删除"),del(1,"已删除");
	
	Deleted(Integer value,String desc) {
		
		this.value = value;
		
		this.desc = desc;
		
	}
	
	@Getter
	@Setter
	private Integer value;
	
	@Getter
	@Setter
	private String desc;

}