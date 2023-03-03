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

package com.maozi.base.api.impl.rpc;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.maozi.factory.result.AbstractBaseResult;

/**
 * 
 * Specifications：功能
 * 
 * Author：彭晋龙
 * 
 * Creation Date：2021-12-18:16:32:34
 *
 * Copyright Ownership：xiao mao zi
 * 
 * Agreement That：Apache 2.0
 * 
 */


public abstract class BaseServiceRpcImpl<M extends MPJBaseMapper<T>, T, D> extends MPJBaseServiceImpl<M, T> {
	
	protected Class<T> doClass;

	protected Class<D> dtoClass;
	
	public BaseServiceRpcImpl() {
		
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		
		doClass = (Class<T>) type.getActualTypeArguments()[1];
		
		dtoClass = (Class<D>) type.getActualTypeArguments()[2];
		
	}

	@Override
	public Class<T> currentModelClass() {
		return super.currentModelClass();
	}

	public AbstractBaseResult<D> rpcGetInfoById(Long id,String ... colums) {
		return success(copy(getById(isNullThrow(id, "此数据"),colums), dtoClass));
	}

	public AbstractBaseResult<List<D>> rpcGetListByIds(Collection<? extends Serializable> ids,String ... colums) {

		return success(copyList(listByIds(isEmptyArray(ids, "数据集"),colums), () -> {

			try {

				return dtoClass.getDeclaredConstructor().newInstance();

			} catch (Exception e) {

				log.error(e.getLocalizedMessage());

				return null;

			}

		}));

	}
	
	public AbstractBaseResult<Map<Long,D>> rpcGetMapsByIds(Collection<? extends Serializable> ids,Function<D, Long> function,String ... colums) {

		List<D> dtos = copyList(listByIds(isEmptyArray(ids, "数据集"),colums), () -> {

			try {

				return dtoClass.getDeclaredConstructor().newInstance();

			} catch (Exception e) {

				log.error(e.getLocalizedMessage());

				return null;

			}

		});
		
		return success(toMapByIds(dtos,function));

	}
	
	public AbstractBaseResult<Long> rpcGetCountByParam(D dto){
		return success(count(new QueryWrapper<T>(copy(dto, doClass))));
	}
	
	public AbstractBaseResult<Void> rpcAdd(D dto){
		
		save(copy(dto, doClass));
		
		return success(null);
		
	}
	
	public AbstractBaseResult<Void> rpcBatchAdd(List<D> dtos){
		
		List<T> dos = copyList(dtos, ()->{
			
			try {
				return doClass.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
				return null;
			}
		});
		
		isNullThrow(dos, "数据集");
		
		dos.parallelStream().forEach(domain ->{
			save(domain);
		});
		
		return success(null);
		
	}

}
