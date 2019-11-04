package com.moutain.factory;

import com.moutain.factory.son.SonBaseInterface;

public  class FactoryBaseInit{

	
	public static <T extends FactoryBase> T init(String name) throws Exception{
		Object newInstance = Class.forName(name).newInstance();
		return (T) newInstance;
	}
	public static void main(String[] args) throws Exception {
		String name="com.moutain.factory.son.FactorySonA";
		FactoryBase init = FactoryBaseInit.init(name);
		SonBaseInterface factory=init.build();
		factory.a();
	}
	
}
 