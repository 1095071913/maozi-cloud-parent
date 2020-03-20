package com.zhongshi.redis.api;
//package com.mountain.redis.api;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import com.mountain.redis.api.fallback.EchoServiceFallback;				
//
///**
// * 功能说明:	测试demo
// * 功能作者:	彭晋龙
// * 创建日期:	2019.8.14
// * 版权归属:	忧伤-蓝河
// */
//
//@FeignClient(value = "mountain-shop-redis",fallback = EchoServiceFallback.class)
//public interface EchoService {
//
//    @PostMapping(value = "/echo")
//    String echo(@RequestParam("message") String message);
//}