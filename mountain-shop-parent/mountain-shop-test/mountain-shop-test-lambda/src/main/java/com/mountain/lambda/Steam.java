package com.mountain.lambda;

import java.util.stream.IntStream;

public class Steam {

	public static void main(String[] args) {
		IntStream.of(new int [5]).parallel().min().getAsInt();
	}
	
}
