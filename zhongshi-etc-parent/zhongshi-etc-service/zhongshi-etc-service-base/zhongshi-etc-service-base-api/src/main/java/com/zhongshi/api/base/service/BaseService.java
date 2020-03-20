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

package com.zhongshi.api.base.service;

import java.util.concurrent.Future;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zhongshi.base.AbstractBaseDomain;

/**
 * 
 * 	功能说明：领域模型API
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-09-06 ：7:09:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */
 
public interface BaseService<T extends AbstractBaseDomain> extends IService<T>{
    Integer insert(T t, String createBy);

    Integer delete(T t);

    Integer update(T t, String updateBy);

    Integer count(T t);
  
//    T selectOne(T t);

    Future<T> selectOne(T t);

    PageInfo<T> page(T t);
    
    
}