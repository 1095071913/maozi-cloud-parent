//package com.mountain.redis.api.fallback;
//
//import org.springframework.stereotype.Component;
//
//import com.mountain.factory.BaseResultFactory;
//import com.mountain.factory.result.AbstractBaseResult;
//import com.mountain.redis.api.RedisService;
//@Component
//public class RedisServiceFallback extends BaseResultFactory implements RedisService{
//
//	public AbstractBaseResult getString(String key) {
//		return getfactory(500, "An exception occurred inside the server");
//	
//	}
//
//	public AbstractBaseResult delKey(String key) {
//		return getfactory(500, "An exception occurred inside the server");
//	}
//
//	public AbstractBaseResult setString(String key, Object data, Long timeout) {
//		return getfactory(500, "An exception occurred inside the server");
//	}
//
//}
