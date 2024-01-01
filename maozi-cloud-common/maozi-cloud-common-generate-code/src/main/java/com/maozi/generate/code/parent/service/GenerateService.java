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

package com.maozi.generate.code.parent.service;

import com.maozi.generate.code.entity.EntityData;
import com.maozi.generate.code.tool.SQLType;
import java.util.List;


/**
 * 
 * 	功能说明：生成Service
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-21 ：11:03:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class GenerateService {
	
	public static void generate(String module,List<EntityData> entityDatas,String pash) throws Exception{
		
		for(EntityData entityData:entityDatas) {
			
			StringBuilder service=new StringBuilder();
			service.append("package "+entityData.getPackageName()+"."+module+"."+entityData.getModuleName()+".api"+";\r\n");
			
			/* 导包 Begin */
			entityImport(service,entityData.getClassName());
			
			service.append("public interface "+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"Service"+(entityData.getClassName()==null?"":" extends MPJBaseService<"+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"Do> ")+"{\r\n");
			service.append("\r\n}");
			
			/* 创建文件 Begin */
			SQLType.fileCreate(pash+"\\"+(entityData.getPackageName().replace(".", "\\"))+"\\"+module+"\\"+entityData.getModuleName()+"\\api", SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"Service", service);
			/* 创建文件 End */
			
			
		}
		
	}
	
	private static void entityImport(StringBuilder entity, String entityClass) {
		
		entity.append("\r\n");
		
		if(entityClass != null) {
			entity.append("import com.github.yulichang.base.MPJBaseService;\r\n");
			entity.append("import "+entityClass+";\r\n");
		}
		
		entity.append("\r\n");
		
	}

}
