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

package com.mountain.generate;

import java.util.List;

import com.mountain.entity.EntityData;
import com.mountain.tool.SQLType;


/**
 * 
 * 	功能说明：生成ServiceImpl
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-20 : 11:53:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class GenerateServiceImpl {

	public static void generate(List<EntityData> entityDatas, String pash) throws Exception {

		for (EntityData entityData : entityDatas) {
			
			StringBuilder serviceImpl = new StringBuilder();
			serviceImpl.append("package " +  entityData.getPackageName()+".api.impl" + ";\r\n");
			
			/* 导包 Begin */
			entityImport(serviceImpl,entityData.getClassName());
			/* 导包 Begin */
			
			serviceImpl.append("public class "+SQLType.initial(entityData.getModuleName())+"ServiceImpl extends BaseServiceImpl<"+SQLType.initial(entityData.getModuleName())+"Do, "+SQLType.initial(entityData.getModuleName())+"Mapper> implements "+SQLType.initial(entityData.getModuleName())+"Service {\r\n");
			
			
			/* code码 Begin */
			code(serviceImpl);
			/* code码 End */
			
			
			serviceImpl.append("\r\n}");
			
			/* 创建文件 Begin */
			SQLType.fileCreate(pash+"\\"+(entityData.getPackageName().replace(".", "\\"))+"\\api\\impl", entityData.getModuleName() + "ServiceImpl", serviceImpl);
			/* 创建文件 End */

		}

	}
	
	
	private static void entityImport(StringBuilder entity, String entityClass) {
		String classPash = entityClass.substring(0, entityClass.lastIndexOf("."));
		String entityName = entityClass.substring(entityClass.lastIndexOf("."), entityClass.length());
		entity.append("\r\n");
		entity.append("import "+entityClass+";\r\n");
		entity.append("import "+classPash+".mapper"+entityName.replace("Do", "Mapper")+";\r\n");
		entity.append("import "+classPash+".api"+entityName.replace("Do", "Service")+";\r\n");
		entity.append("import com.mountain.api.base.service.impl.BaseServiceImpl;\r\n");
		entity.append("\r\n");
	}
	
	
	
	private static void code(StringBuilder entity) {
		entity.append("\r\n");
		entity.append("	static {\r\n");
		entity.append("\r\n");
		entity.append("		code(new CodeHashMap(applicationName,port) {{\r\n");
		entity.append("\r\n");
		entity.append("\r\n");
		entity.append("\r\n");
		entity.append("		}});"+"\r\n");
		entity.append("\r\n");
		entity.append("	}\r\n");
		entity.append("\r\n");
		entity.append("\r\n");
	}

}
