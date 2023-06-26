package com.maozi.base.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**  
 * @Title: QueryRelation.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-06-22 08:15:22
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryRelation {
	
	boolean isService() default false;
	
	String serviceName() default "";
	
	String relationField() default "";
	
	boolean ignore() default false;
	
	String functionName() default "";

}
