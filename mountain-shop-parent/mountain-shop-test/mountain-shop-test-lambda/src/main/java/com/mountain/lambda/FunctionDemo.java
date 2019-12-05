package com.mountain.lambda;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class FunctionDemo {

	public static void main(String[] args) {
		
		
		/* 断言函数接口 Begin */
		Predicate<Integer> predicate = i -> i>0;
		System.out.println(predicate.test(-9));
		
		IntPredicate predicate2 = i -> i>0;
		/* 断言函数接口 End */
		
		
		/* 消费函数接口 Begin */
		Consumer<String> consumer = System.out::println;
		consumer.accept("请输入");
		
		IntConsumer consumer2 = s -> System.out.println(s);
		/* 消费函数接口 End */
		
		
	}

}
