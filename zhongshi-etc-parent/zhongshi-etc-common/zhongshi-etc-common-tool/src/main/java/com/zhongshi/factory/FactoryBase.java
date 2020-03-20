package com.zhongshi.factory;

import com.zhongshi.factory.son.SonBaseInterface;

public abstract class FactoryBase implements SonBaseInterface{

	protected abstract SonBaseInterface buildFactory();
	
	public SonBaseInterface build(){
		return buildFactory();
	}

	
}


