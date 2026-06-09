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

/**
 * 百度地图服务实现
 * <p>
 * 封装百度地图 API 的 HTTP 调用逻辑，自动注入 API 密钥，
 * 支持 GET 和 POST 请求方式，自动处理错误响应。
 * </p>
 *
 * @author maozi
 */
public class BDServiceImpl implements BDService{

    /** 百度地图配置属性 */
	@Resource
	protected BDProperties bdProperties;

    /** REST 客户端 */
	@Resource
	private RestTemplate restClient;

    /**
     * 调用百度地图 REST API
     *
     * @param uri 请求路径
     * @param privateParam 私有参数
     * @param method HTTP 方法
     * @return 百度 API 响应结果
     */
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
