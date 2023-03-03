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

package com.maozi.wx.api.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONObject;
import com.maozi.factory.BaseResultFactory;
import com.maozi.factory.result.code.CodeAttribute;
import com.maozi.factory.result.error.exception.BusinessResultException;
import com.maozi.mvc.config.rest.RestTemplate;
import com.maozi.wx.api.WxService;
import com.maozi.wx.properties.WxProperties;

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

public class WxServiceImpl extends BaseResultFactory implements WxService{
	
	@Resource
	protected RestTemplate restClient;
	
	@Resource
	protected WxProperties wxProperties;
	
	@Override
	public JSONObject vxRest(String url,Map<String, Object> body,HttpMethod method) {
		
		ResponseEntity<JSONObject> vxResult = null;
		
		if(HttpMethod.GET.equals(method)) {
			
			vxResult = restClient.getForEntity(url, JSONObject.class,body);
			
		}else {
			
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(body, new HttpHeaders());
			vxResult = restClient.postForEntity(url,requestEntity,JSONObject.class);
			
		}
		
		if(isNull(vxResult)) {
			throw new BusinessResultException(new CodeAttribute(500,"企业微信服务不可用",500));
		}
		
		if(isNull(vxResult) || vxResult.getStatusCodeValue() != 200 || vxResult.getBody().getInteger("errcode")!=0) {
			throw new BusinessResultException(new CodeAttribute(vxResult.getBody().getInteger("errcode"),vxResult.getBody().getString("errmsg")),vxResult.getStatusCodeValue());
		}
		
		return vxResult.getBody();
		
	}
	
	@Override
	public String getVxAccessToken() {
		
		Map<String, Object> getTokenBody = new HashMap<String, Object>(){
			{
				put("corpid", wxProperties.getCorpid());
				put("corpsecret",wxProperties.getContactsCorpsecret());
			}
		};
		JSONObject vxGetTokenResultData = vxRest("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpid}&corpsecret={corpsecret}",getTokenBody,HttpMethod.GET);
		
		return vxGetTokenResultData.getString("access_token");
	}

	@Override
	public String getDocumentData(Document document,String tag) {
		
		Element root = document.getDocumentElement();   
		
		NodeList nodelist1 = root.getElementsByTagName(tag);
		
		return nodelist1.item(0).getTextContent(); 
		
	}
	
}