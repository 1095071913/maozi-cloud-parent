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

package com.maozi.qny.api.impl.rest;

import com.maozi.base.annotation.Get;
import com.maozi.base.annotation.RestService;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.qny.properties.QNYProperties;
import com.qiniu.util.Auth;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestParam;

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

@RestService
@Tag(name = "【全局】图片管理")
public class OssServiceRestImpl extends BaseCommon {

	private static final String PATH = "/oss";
	
	@Resource
	private QNYProperties qnyProperties;

	@Get(value = PATH + "/getToken",description = "获取七牛云图片访问令牌")
	public AbstractBaseResult<String> getToken(@RequestParam String url) throws Exception{
		
		Auth auth = Auth.create(qnyProperties.getAccessKey(), qnyProperties.getSecretKey());
		
		return success(auth.privateDownloadUrl(url, 86400));
		
	}
	
}
