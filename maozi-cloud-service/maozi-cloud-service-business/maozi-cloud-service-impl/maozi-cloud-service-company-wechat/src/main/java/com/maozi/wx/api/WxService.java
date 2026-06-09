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

package com.maozi.wx.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.w3c.dom.Document;

import java.util.Map;

/**
 * 企业微信服务接口
 * <p>
 * 定义企业微信 API 的调用方法，包括 XML 数据解析、
 * Access Token 获取和 REST API 调用。
 * </p>
 *
 * @author maozi
 */
public interface WxService {

    /**
     * 从 XML 文档中提取指定标签的值
     *
     * @param document XML 文档
     * @param tag 标签名
     * @return 标签值
     */
	String getDocumentData(Document document, String tag);

    /**
     * 获取企业微信 Access Token
     *
     * @return Access Token 字符串
     */
	String getVxAccessToken();

    /**
     * 调用企业微信 REST API
     *
     * @param url 请求 URL
     * @param body 请求参数
     * @param method HTTP 方法
     * @return API 响应结果
     */
	JSONObject vxRest(String url, Map<String, Object> body, HttpMethod method);

}
