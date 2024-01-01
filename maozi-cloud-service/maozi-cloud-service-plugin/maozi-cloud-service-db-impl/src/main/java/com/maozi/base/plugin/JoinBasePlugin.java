package com.maozi.base.plugin;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.join.JoinPlugin;

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
public abstract class JoinBasePlugin<T> {

	public abstract void apply(String abbreviationModelName,MPJLambdaWrapper<T> wrapper, JoinPlugin joinPlugin);
	
}
