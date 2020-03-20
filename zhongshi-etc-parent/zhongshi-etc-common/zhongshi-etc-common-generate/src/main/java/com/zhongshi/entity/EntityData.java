
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

package com.zhongshi.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 
 * 	功能说明：Do数据包
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-20 7:07:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */



@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityData {
	
	public EntityData(String className, List<String> field, String moduleName, String packageName) {
		super();
		this.className = className;
		this.field = field;
		this.moduleName = moduleName;
		this.packageName = packageName;
	}

	private String className;
	
	private List<String> field;
	
	private String moduleName;
	
	private String packageName;
	
	private List<EntityData> foreignEntityOne;
	
	private List<EntityData> foreignEntityMany;
	
}
