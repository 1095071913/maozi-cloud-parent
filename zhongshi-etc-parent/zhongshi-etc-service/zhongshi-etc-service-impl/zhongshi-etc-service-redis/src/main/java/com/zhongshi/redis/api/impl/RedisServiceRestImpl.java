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

package com.zhongshi.redis.api.impl;

import javax.annotation.Resource;

import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.code.CodeHashMap;
import com.zhongshi.redis.api.RedisServiceRest;
import com.zhongshi.redis.config.RedisUtils;

/**
 * 
 * 	功能说明：Redis Rest实现
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-31 ：20:16:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class RedisServiceRestImpl extends BaseResultFactory implements RedisServiceRest{
	
	static {
		
		code(new CodeHashMap(applicationName,port) {{
			
			this.put(new CodeAttribute(60001,"传入的Key为空"));
			
			this.put(new CodeAttribute(60002,"传入的Key没有对应的Value"));
			
			this.put(new CodeAttribute(60003,"删除失败"));
			
			this.put(new CodeAttribute(60004,"添加失败"));

			this.put(new CodeAttribute(60005,"传入的数据为空"));
			
		}});
		
	}
	
	
    
	@Resource
	private RedisUtils redisUtils;
	
	
	
	
	
	@Override
	public AbstractBaseResult getString(String key) {
		
		
		if(isNotNull(key)){
			return error(code(60001));
		}
		
		String value = redisUtils.getString(key);
		
		if(isNotNull(value)){
			return error(code(60002));
		}
		
		return success(value);
		
		
	}
	@Override
	public AbstractBaseResult delKey(String key) {
		
		if(isNotNull(key)){
			return error(code(60001));
		}
		
		if(redisUtils.delKey(key)){
			return success("删除成功");
		}
		
		return error(code(60003));
	}
	
	
	@Override
	public AbstractBaseResult setString(String key, Object data, Long timeout) {
		if(isNotNull(key)){
			return error(code(60001));
		}
		if(isNotNull(data)){
			return error(code(60005));
		}
		if(redisUtils.setString(key, data, timeout)){ 
			return success("添加成功");
		}
		return error(code(60004));
	}
	
}
