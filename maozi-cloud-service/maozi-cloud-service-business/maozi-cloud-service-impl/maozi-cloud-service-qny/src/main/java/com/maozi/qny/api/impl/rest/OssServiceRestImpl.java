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
import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.qny.properties.QNYProperties;
import com.qiniu.util.Auth;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestParam;


@RestService
@Tag(name = "【全局】/【对象存储】")
public class OssServiceRestImpl {

	private static final String PATH = "/oss";
	
	@Resource
	private QNYProperties qnyProperties;

	@Get(value = PATH + "/getToken",description = "获取七牛云图片访问令牌")
	public AbstractBaseResult<String> getToken(@RequestParam String url){
		
		Auth auth = Auth.create(qnyProperties.getAccessKey(), qnyProperties.getSecretKey());
		
		return ResultUtil.success(auth.privateDownloadUrl(url, 86400));
		
	}
	
}
