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

package com.maozi.ss.api;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * 闪送服务接口
 * <p>
 * 定义闪送开放平台 API 的调用方法，包括 REST 调用和 Token 管理。
 * </p>
 *
 * @author maozi
 */
public interface SSService {

    /**
     * 调用闪送 REST API（带 Token 刷新）
     *
     * @param uri 请求路径
     * @param privateParam 私有参数
     * @param refreshToken 刷新令牌
     * @return API 响应结果
     */
	JSONObject ssRefreshRest(String uri,Map<String, Object> privateParam,String refreshToken);

    /**
     * 调用闪送 REST API
     *
     * @param uri 请求路径
     * @param privateParam 私有参数
     * @param accessToken 访问令牌
     * @return API 响应结果
     */
	JSONObject ssRest(String uri,Map<String, Object> privateParam,String accessToken);

    /**
     * 通过授权码获取访问令牌
     *
     * @param code 授权码
     * @return 令牌信息
     */
	JSONObject ssRestGetToken(String code);

}
