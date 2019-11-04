package com.moutain.factory;

import com.moutain.factory.son.SonBaseInterface;

public abstract class FactoryBase implements SonBaseInterface{

	protected abstract SonBaseInterface buildFactory();
	
	public SonBaseInterface build(){
		return buildFactory();
	}

	
}


