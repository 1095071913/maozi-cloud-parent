package com.maozi.base.plugin.impl.query;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.QueryBasePlugin;

/**  
 * @Title: QueryEqPlugin.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-06-21 09:09:44
 *
 */
public class QueryLikePlugin<T> extends QueryBasePlugin<T>{

	@Override
	public void apply(MPJLambdaWrapper<T> wrapper,String field,Object data) {
		wrapper.likeRight(field, data);
	}

}
