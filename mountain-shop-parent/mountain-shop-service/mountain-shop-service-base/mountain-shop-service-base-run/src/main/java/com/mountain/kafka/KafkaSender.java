//package com.mountain.kafka;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//
//import com.alibaba.fastjson.JSON;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 功能说明:	Kafka发送消息
// * 功能作者:	彭晋龙
// * 创建日期:	2019.8.14
// * 版权归属:	忧伤-蓝河
// */
//
//@Component
//@Slf4j
//public class KafkaSender<T> {
//
//	@Autowired
//	private KafkaTemplate<String, Object> kafkaTemplate;
//
//	public void send(T obj) {
//		String jsonObj = JSON.toJSONString(obj); 
//		log.info("------------ message = {}", jsonObj);  
//		// 发送消息 实现可配置化 主题是可配置化
//		ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("goods_mylog", jsonObj);
//		future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//			@Override
//			public void onFailure(Throwable throwable) { 
//				log.info("Produce: The message failed to be sent:" + throwable.getMessage());
//			}
//
//			@Override
//			public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
//				// TODO 业务处理
//				log.info("Produce: The message was sent successfully:");
//				log.info("Produce: _+_+_+_+_+_+_+ result: " + stringObjectSendResult.toString());
//			}
//		});
//	}
//}