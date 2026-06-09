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

package com.maozi.bd.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * 百度地图服务接口
 * <p>
 * 定义百度地图 API 的 REST 调用方法。
 * </p>
 *
 * @author maozi
 */
public interface BDService {

    /**
     * 调用百度地图 REST API
     *
     * @param uri 请求路径
     * @param privateParam 私有参数
     * @param method HTTP 方法
     * @return 百度 API 响应结果
     */
	JSONObject bdRest(String uri,Map<String,Object> privateParam,HttpMethod method);

}
