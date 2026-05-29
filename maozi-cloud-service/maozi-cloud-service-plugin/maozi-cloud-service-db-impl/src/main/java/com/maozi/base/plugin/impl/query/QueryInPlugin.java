package com.maozi.base.plugin.impl.query;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.QueryBasePlugin;

public class QueryInPlugin extends QueryBasePlugin {

	@Override
	public void apply(MPJLambdaWrapper<?> wrapper,String field,Object data) {
		wrapper.in(field, data);
	}

}
