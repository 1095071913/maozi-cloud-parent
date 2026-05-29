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

package com.maozi.bd.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.maozi.bd.api.BDService;
import com.maozi.bd.properties.BDProperties;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.mvc.config.rest.RestTemplate;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;


public class BDServiceImpl implements BDService{

	@Resource
	protected BDProperties bdProperties;
	
	@Resource
	private RestTemplate restClient;
	
	@Override
	public JSONObject bdRest(String uri,Map<String,Object> privateParam,HttpMethod method) {
		
		if(ObjectUtil.isNullEmpty(privateParam)) {
			privateParam = CollectionUtil.newHashMap();
		}
		
		privateParam.put("ak", bdProperties.getAk());
		
		ResponseEntity<String> bdResult = null;
		if(HttpMethod.GET.equals(method)) {
			
			bdResult = restClient.getForEntity(bdProperties.getUrl()+uri+"&ak={ak}&output=json", String.class,privateParam);
			
		}else {
			
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(privateParam, new HttpHeaders());
			bdResult = restClient.postForEntity(bdProperties.getUrl()+uri,requestEntity,String.class);
			
		}
		
		JSONObject response = JSONObject.parseObject(bdResult.getBody());

		if(ObjectUtil.isNullEmpty(bdResult)) {
			throw new BusinessResultException(new ErrorCode(500,"百度服务不可用"));
		}

		if(ObjectUtil.isNullEmpty(bdResult) || bdResult.getStatusCodeValue() != 200 || response.getInteger("status")!=0) {
			throw new BusinessResultException(new ErrorCode(response.getInteger("status"),response.getString("message")));
		}
		
		return response; 
		
	}
	
}
