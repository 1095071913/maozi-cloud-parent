package com.moutain.factory.son;

public interface SonBaseInterface {
	
	public default SonBaseInterface a() {
		return init();
	}

	SonBaseInterface init();
}
