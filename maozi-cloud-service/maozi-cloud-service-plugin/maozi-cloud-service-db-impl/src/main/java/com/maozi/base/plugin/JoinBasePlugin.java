package com.maozi.base.plugin;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.base.plugin.join.JoinPlugin;

public abstract class JoinBasePlugin {

	public abstract void apply(String abbreviationModelName,MPJLambdaWrapper<?> wrapper, JoinPlugin joinPlugin);
	
}
