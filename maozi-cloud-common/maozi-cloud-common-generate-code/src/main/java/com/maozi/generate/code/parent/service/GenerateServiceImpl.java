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

	public static void generate(String module,List<EntityData> entityDatas, String pash) throws Exception {

		for (EntityData entityData : entityDatas) {
			
			StringBuilder serviceImpl = new StringBuilder();
			serviceImpl.append("package " +  entityData.getPackageName()+"."+module+"."+entityData.getModuleName()+".api.impl" + ";\r\n");
			
			/* 导包 Begin */
			entityImport(serviceImpl,entityData.getClassName(),module,entityData.getModuleName());
			/* 导包 Begin */
			
			serviceImpl.append("public class "+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"ServiceImpl"+(entityData.getClassName()==null?"":" extends BaseServiceImpl<"+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"Mapper, "+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"Do,Void>")+" implements "+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName()))+"Service {\r\n\r\n");
			
			serviceImpl.append("	@Override\r\n"
					+ "	protected String getAbbreviationModelName() {\r\n"
					+ "		return null;\r\n"
					+ "	}\r\n");
			
			serviceImpl.append("\r\n}");
			
			/* 创建文件 Begin */
			SQLType.fileCreate(pash+"\\"+(entityData.getPackageName().replace(".", "\\"))+"\\"+module+"\\"+entityData.getModuleName()+"\\api\\impl", SQLType.initial(SQLType.underlineToCapital(entityData.getTableName())) + "ServiceImpl", serviceImpl);
			/* 创建文件 End */

		}

	}
	
	
	private static void entityImport(StringBuilder entity, String entityClass,String module,String childModule) {
		
		entity.append("\r\n");
		
		if(entityClass != null) {
			String entityName = entityClass.substring(entityClass.lastIndexOf("."), entityClass.length());
			entity.append("import "+entityClass+";\r\n");
			entity.append("import com.maozi"+"."+module+"."+childModule+".mapper"+entityName.replace("Do", "Mapper")+";\r\n");
			entity.append("import com.maozi"+"."+module+"."+childModule+".api"+entityName.replace("Do", "Service")+";\r\n");
			entity.append("import com.maozi.base.api.impl.BaseServiceImpl;\r\n");
		}else {
			entity.append("import com.maozi"+"."+module+"."+childModule+".api."+SQLType.initial(childModule)+"Service"+";\r\n");
		}
		
		entity.append("\r\n");
	}
	

}
