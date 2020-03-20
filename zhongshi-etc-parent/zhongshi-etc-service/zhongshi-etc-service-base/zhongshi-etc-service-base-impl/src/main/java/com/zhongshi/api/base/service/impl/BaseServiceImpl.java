
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

package com.zhongshi.api.base.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhongshi.api.base.service.BaseService;
import com.zhongshi.base.AbstractBaseDomain;

import java.util.Date;
import java.util.concurrent.Future;

import javax.annotation.Resource;

/**
 * 
 * 	功能说明：领域模型API实现
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-09-25 : 4:56:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Transactional(readOnly = true)
public abstract class BaseServiceImpl<T extends AbstractBaseDomain, D extends BaseMapper<T>> extends ServiceImpl<D, T> implements BaseService<T> {

	@Resource
	private D dao;

	@Async
	@Override
	@Transactional(readOnly = false)
	public Integer insert(T t, String createBy) {
		Date date = new Date();
		t.setCreateBy(createBy);
		t.setCreateDate(date);
		t.setUpdateBy(createBy);
		t.setUpdateDate(date);
		return dao.insert(t);
	}

	@Override
	@Transactional(readOnly = false)
	public Integer delete(T t) {
		return dao.deleteById(t.getId());
	}

	@Override
	@Transactional(readOnly = false)
	public Integer update(T t, String updateBy) {
		t.setUpdateBy(updateBy);
		t.setUpdateDate(new Date());
		return dao.updateById(t);
	}

	@Override
	public Integer count(T t) {
		return dao.selectCount(new QueryWrapper<T>(t));
	}

     
//	@Override
//	public T selectOne(T t) {
//		return dao.selectOne(new QueryWrapper<T>(t));   
//	}
	
    @Async
	@Override
	public Future<T> selectOne(T t) {
		return new AsyncResult<>(dao.selectOne(new QueryWrapper<T>(t)));
	}

	@Override  
	public PageInfo<T> page(T t) {
		PageHelper.startPage(t.getPageBegin(), t.getPageSize());
		PageInfo<T> pageInfo = new PageInfo<>(dao.selectList((new QueryWrapper<T>(t))));
		return pageInfo;
	}
	
	

}