package com.maozi.base.plugin.impl.query;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.QueryBasePlugin;

public class QueryLikePlugin extends QueryBasePlugin{

	@Override
	public void apply(MPJLambdaWrapper<?> wrapper,String field,Object data) {
		wrapper.likeRight(field, data);
	}

}
