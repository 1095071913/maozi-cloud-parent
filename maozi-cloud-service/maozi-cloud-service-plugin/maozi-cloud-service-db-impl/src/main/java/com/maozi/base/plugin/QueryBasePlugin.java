package com.maozi.base.plugin;

import com.github.yulichang.wrapper.MPJLambdaWrapper;

public abstract class QueryBasePlugin {

	public abstract void apply(MPJLambdaWrapper<?> wrapper,String field,Object data);
	
}
