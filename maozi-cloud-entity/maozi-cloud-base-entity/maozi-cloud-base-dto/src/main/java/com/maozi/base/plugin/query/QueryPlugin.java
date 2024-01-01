package com.maozi.base.plugin.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**  
 * @Title: QueryPlugin.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-06-21 09:15:59
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryPlugin {
	
	String field() default "";
	
	QueryBaseType value();
	
	boolean nest() default false;

	String tableName() default "";

}
