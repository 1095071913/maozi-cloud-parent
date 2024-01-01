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

package com.maozi.ss.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.code.CodeAttribute;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.mvc.config.rest.RestTemplate;
import com.maozi.ss.api.SSService;
import com.maozi.ss.config.SSConfig;
import com.maozi.ss.properties.SSProperties;
import com.maozi.utils.MapperUtils;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**	
 * 
 *  Specifications：功能
 * 
 *  Author：彭晋龙
 * 
 *  Creation Date：2021-12-18:16:32:34
 *
 *  Copyright Ownership：xiao mao zi
 * 
 *  Agreement That：Apache 2.0
 * 
 */

public class SSServiceImpl extends BaseCommon implements SSService{
	
	@Resource
	private RestTemplate restClient;
	
	@Resource
	private SSProperties ssProperties;
	
	@Override
	public JSONObject ssRefreshRest(String uri,Map<String, Object> privateParam,String refreshToken) {
		
		Map<String,Object> refreshTokenParam = new HashMap<String, Object>(){{
			put("refreshToken", refreshToken);
		}};
		
		JSONObject ssRest = ssRest("/openapi/oauth/refresh_token",refreshTokenParam,null);
		
		String accessToken = ssRest.getJSONObject("data").getString("access_token");
		
		return ssRest(uri,privateParam,accessToken);
		
	}

	@Override
	public JSONObject ssRest(String uri, Map<String, Object> privateParam, String accessToken) {
		
		String privateParamJson = MapperUtils.mapToJson(privateParam);
		
		Long currentTimeMillis = System.currentTimeMillis();
		
		StringBuffer sb = new StringBuffer(ssProperties.getAppSecret());
		
		if(isNotNull(accessToken)) {
			sb.append("accessToken").append(accessToken);
		}
        		
        sb.append("clientId").append(ssProperties.getClientId())
          .append("data").append(privateParamJson) 
          .append("timestamp").append(currentTimeMillis);
		
        MultiValueMap<String, Object> publicParam = new LinkedMultiValueMap<String, Object>(){{
        	
        	if(isNotNull(accessToken)) {
        		add("accessToken", accessToken);
        	}
			add("clientId", ssProperties.getClientId());
			add("data", privateParamJson);  
			add("timestamp", currentTimeMillis);
			add("sign",SSConfig.bytesToMD5(sb.toString().getBytes()));
		}}; 
		     
		HttpHeaders headers = new HttpHeaders();       
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);        
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(publicParam, headers); 
		 
		ResponseEntity<JSONObject> ssResult = restClient.postForEntity(ssProperties.getUrl()+uri,request,JSONObject.class);
			
		
		if(isNull(ssResult)) {
			throw new BusinessResultException(new CodeAttribute(500,"闪送服务不可用",500));
		}
		
		if(ssResult.getStatusCodeValue() != 200 || ssResult.getBody().getInteger("status")!=200) {
			throw new BusinessResultException(new CodeAttribute(ssResult.getBody().getInteger("status"),ssResult.getBody().getString("msg")),400);
		}
		
		return ssResult.getBody();
	}

	@Override
	public JSONObject ssRestGetToken(String code) {
		
		MultiValueMap<String, Object> publicParam = new LinkedMultiValueMap<String, Object>(){{
        	
			add("clientId", ssProperties.getClientId());
			add("code", code);
        	
		}}; 
		     
		HttpHeaders headers = new HttpHeaders();       
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);        
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(publicParam, headers); 
		 
		ResponseEntity<JSONObject> ssResult = restClient.postForEntity(ssProperties.getUrl()+"/openapi/oauth/token",request,JSONObject.class);
			
		
		if(isNull(ssResult)) {
			throw new BusinessResultException(new CodeAttribute(500,"闪送服务不可用",500));
		}
		
		if(ssResult.getStatusCodeValue() != 200 || ssResult.getBody().getInteger("status")!=200) {
			throw new BusinessResultException(new CodeAttribute(ssResult.getBody().getInteger("status"),ssResult.getBody().getString("msg")),400);
		}
		
		return ssResult.getBody().getJSONObject("data");
	}


}
