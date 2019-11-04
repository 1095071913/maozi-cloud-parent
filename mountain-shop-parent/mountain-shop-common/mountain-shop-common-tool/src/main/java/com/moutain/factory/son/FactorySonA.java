package com.moutain.factory.son;

import com.moutain.factory.FactoryBase;

public class FactorySonA<T extends SonA> extends FactoryBase implements SonA{

	@Override
	public SonA buildFactory() {
		System.out.println("初始化A工厂");
		return this;
	}

	@Override
	public T init() {
		System.out.println("A工厂子类初始化");
		return (T)this;
	}

}
