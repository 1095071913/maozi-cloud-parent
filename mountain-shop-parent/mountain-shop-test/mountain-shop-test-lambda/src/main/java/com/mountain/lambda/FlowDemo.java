//package com.mountain.lambda;
//
//import java.util.concurrent.Flow.Processor;
//import java.util.concurrent.Flow.Subscriber; 
//import java.util.concurrent.Flow.Subscription;
//import java.util.concurrent.SubmissionPublisher;
//
//public class FlowDemo {
//
//	public static void main(String[] args) throws InterruptedException {
//		
//		SubmissionPublisher<String> publiser=new SubmissionPublisher<String>();
//		
//		MyProcessor myProcessor=new MyProcessor();
//		publiser.subscribe(myProcessor);
//		
//		Subscriber<String> subscriber=new Subscriber<String>() {
//
//			private Subscription subscription;
//			
//			@Override
//			public void onSubscribe(Subscription subscription) {
//				this.subscription=subscription;
//				this.subscription.request(1);
//			}
//
//			@Override
//			public void onNext(String item) {
//				System.out.println("接收到数据");
//				this.subscription.request(1);
//				
//				//this.subscription.cancel();  告诉发布者不再接收数据
//			}
//
//			@Override
//			public void onError(Throwable throwable) {
//				throwable.printStackTrace();
//				this.subscription.cancel();
//			}
//
//			@Override
//			public void onComplete() {
//				System.out.println("数据处理完毕");
//			}
//		};
//		
//		myProcessor.subscribe(subscriber);
//		
//		publiser.subscribe(subscriber);
//		
//		publiser.submit("");
//		
//		publiser.close();
//		
//		Thread.currentThread().join(1000);
//	}
//	
//
//}
//
//
//class MyProcessor extends SubmissionPublisher<String> implements Processor<String, String>{
//
//	private Subscription subscription;
//	
//	@Override
//	public void onSubscribe(Subscription subscription) {
//		this.subscription=subscription;
//		this.subscription.request(1);
//	}
//
//
//	@Override
//	public void onNext(String item) {
//		System.out.println("处理器接收到数据"+item);
//		if(item!=null) {
//			this.submit("转换后的数据"+item);
//		}
//		this.subscription.request(1);
//	}
//
//	@Override
//	public void onError(Throwable throwable) {
//		
//	}
//
//	@Override
//	public void onComplete() {
//		// TODO Auto-generated method stub
//		
//	}
//	
//}