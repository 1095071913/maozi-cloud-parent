package com.zhongshi.factory.son;

import com.zhongshi.factory.FactoryBase;

public class FactorySonB<T extends SonB> extends FactoryBase implements SonB{

	@Override
	protected SonB buildFactory() {
		System.out.println("B工厂子类初始化");
		return this;
	}

	@Override
	public T init() {
		System.out.println("初始化B工厂");
		return (T)this;
	}

}
