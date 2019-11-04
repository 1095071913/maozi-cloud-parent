package com.mountain.redis.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.redis.api.fallback.RedisServiceFallback;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@FeignClient(value = "mountain-shop-redis", fallback = RedisServiceFallback.class)
@Api("Redis接口")
public interface RedisService {

	@PostMapping("/getString")
	@ApiOperation(value = "Redis中获取")
	AbstractBaseResult getString(@RequestParam("key") String key);

	@PostMapping("/delKey")
	@ApiOperation(value = "Redis中删除")
	AbstractBaseResult delKey(@RequestParam("key") String key);

	@PostMapping("/setString")
	@ApiOperation(value = "Redis中设值")
	AbstractBaseResult setString(@RequestParam("key") String key, @RequestParam("data") @RequestBody Object data,
			@RequestParam(value = "timeout", required = false) Long timeout);

}
