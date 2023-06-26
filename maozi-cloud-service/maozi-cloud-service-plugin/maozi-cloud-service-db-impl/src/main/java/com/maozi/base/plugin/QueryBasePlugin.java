package com.maozi.base.plugin;

import com.github.yulichang.wrapper.MPJLambdaWrapper;

/**  
 * @Title: QueryBasePlugin.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-06-21 09:03:17
 *
 */
public abstract class QueryBasePlugin<T> {

	public abstract void apply(MPJLambdaWrapper<T> wrapper,String field,Object data);
	
}
