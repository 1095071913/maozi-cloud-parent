package com.mountain.lambda;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class MethodRefrenceDemo {
	
	public MethodRefrenceDemo(Integer i) {
		
	}
	
	public MethodRefrenceDemo() {
		
	}
	
	public String name="Dog";
	
	public static void showDog(MethodRefrenceDemo demo) {
		System.out.println(demo.name+"叫了");
	}
	public Integer showDog(MethodRefrenceDemo this,Integer i) {
		return i;
	}

	public static void main(String[] args) {
		
		Consumer<String> consumer = System.out::println;
		consumer.accept("请输入");
		
		MethodRefrenceDemo methodRefrenceDemo = new MethodRefrenceDemo();
		methodRefrenceDemo.showDog(1);
		
		Consumer<MethodRefrenceDemo> consumer2 = MethodRefrenceDemo::showDog;
		consumer2.accept(methodRefrenceDemo);
		
		Function<Integer,Integer> consumer3= methodRefrenceDemo::showDog;
		UnaryOperator<Integer> consumer4= methodRefrenceDemo::showDog;
		System.out.println(consumer3.apply(1));
		
		BiFunction<MethodRefrenceDemo, Integer, Integer> function = MethodRefrenceDemo::showDog;
		function.apply(methodRefrenceDemo, 1);
		
		Supplier<MethodRefrenceDemo> supplier = MethodRefrenceDemo::new;
		MethodRefrenceDemo methodRefrenceDemo3 = supplier.get();
		
		Function<Integer,MethodRefrenceDemo> function2 = MethodRefrenceDemo::new;
		function2.apply(1);
	}

}
