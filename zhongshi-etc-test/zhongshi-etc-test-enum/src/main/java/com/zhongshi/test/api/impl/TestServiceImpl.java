
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

package com.zhongshi.test.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.ibatis.reflection.property.PropertyNamer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.zhongshi.api.base.service.impl.ServiceImpl;
import com.zhongshi.factory.result.code.CodeAttribute;
import com.zhongshi.factory.result.code.CodeHashMap;
import com.zhongshi.factory.result.code.IBaseEnum;
import com.zhongshi.test.api.TestService;
import com.zhongshi.test.domain.TestDo;
import com.zhongshi.test.enums.TestEnum1;
import com.zhongshi.test.mapper.TestMapper;

import cn.hutool.extra.cglib.CglibUtil;
  
/**
 * 
 * 功能说明：用户服务实现
 * 
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 * 创建日期：2019-08-03 ：1:32:00
 *
 * 版权归属：蓝河团队
 *
 * 协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class TestServiceImpl extends ServiceImpl<TestMapper, TestDo> implements TestService {

	static{

		code(new CodeHashMap() {

			{
				
				this.put(new CodeAttribute<String>(3001, "图形认证码不正确"));
			}

		});

	}
	
	public List<TestDo> getTestDo() {
		return list();
	}
	
}
