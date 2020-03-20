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

package com.zhongshi.factory.result.code;

import java.io.Serializable;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 	功能说明：装载Code信息码HashMap
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-01 03:56:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeHashMap extends HashMap<Integer,CodeAttribute> implements Serializable{
	
	private String applicationName;
	private Integer port;
	
	 public void put(CodeAttribute codeAttribute) {
		 
		if(port<=codeAttribute.getCode() && (port+10000)>=codeAttribute.getCode() ) {
			codeAttribute.setMessage(applicationName+"------"+codeAttribute.getMessage());
			super.put(codeAttribute.getCode(), codeAttribute);
		}
		
    }
	 

}
