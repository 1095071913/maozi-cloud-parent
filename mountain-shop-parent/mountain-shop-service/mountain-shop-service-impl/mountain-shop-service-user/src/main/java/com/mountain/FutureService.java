package com.mountain;

import java.util.concurrent.Future;

public interface FutureService {
	
	Future<String> futureTest() throws InterruptedException;
	
}
