/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.mountain.redis.config;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 
 * 	功能说明：Redis配置
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Component
public class RedisUtils {
	
	
	@Resource
    private RedisTemplate<String, String> stringRedisTemplate;

	public Boolean setString(String key, Object data, Long timeout) {
		if (data instanceof String) {
			String value = (String) data;
			stringRedisTemplate.opsForValue().set(key, value);
			return true;
		}
		if (timeout != null) {
			return stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		}
		return false;
	}

	public String getString(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	public Boolean delKey(String key) {
		return stringRedisTemplate.delete(key);
	}
	
	/**
	 * 开启Redis 事务
	 * 
	 * @param isTransaction
	 */
	public void begin() {
		// 开启Redis 事务权限
		stringRedisTemplate.setEnableTransactionSupport(true);
		// 开启事务
		stringRedisTemplate.multi();

	}

	/**
	 * 提交事务
	 * 
	 * @param isTransaction
	 */
	public void exec() {
		// 成功提交事务
		stringRedisTemplate.exec();
	}

	/**
	 * 回滚Redis 事务
	 */
	public void discard() {
		stringRedisTemplate.discard();
	}

}