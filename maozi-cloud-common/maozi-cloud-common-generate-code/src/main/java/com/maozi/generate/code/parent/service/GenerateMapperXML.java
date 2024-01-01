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
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 * 
 * 	功能说明：生成Mapper XML
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class GenerateMapperXML {

	
	public static void generate(String module,List<EntityData> entityDatas, String pash) throws Exception {

		for (EntityData entityData : entityDatas) {

			Document doc = DocumentHelper.createDocument();
			
			/* dtd文件引用 Begin */
			doc.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd","");
			/* dtd文件引用 End */
			
			/* mapper节点 Begin */
			Element mapperXML = DocumentHelper.createElement("mapper");
			mapperXML.addAttribute("namespace", entityData.getPackageName()+"."+module+"."+entityData.getModuleName()+".mapper."+SQLType.initial(SQLType.underlineToCapital(entityData.getTableName())) + "Mapper");
			/* mapper节点 End */
			
			
			
			
			/* 防止SQL查询命名冲突切入元素 Begin */
			Element sqlEntityColumnXML = DocumentHelper.createElement("sql");
			sqlEntityColumnXML.addAttribute("id", SQLType.underlineToCapital(entityData.getTableName())+"Column");
			String entityColumn="";
			for(String column:entityData.getField()) {
				if(entityColumn!="") {
					entityColumn+=",";
				}
				entityColumn+=entityData.getModuleName()+"."+column;
				
			}
			sqlEntityColumnXML.addText(entityColumn);
			mapperXML.add(sqlEntityColumnXML);
			/* 防止SQL查询命名冲突切入元素 End */
			
			
//			/* 一对一 Begin */
//			if(!StringUtils.isEmpty(entityData.getForeignEntityOne())) {
//				for(EntityData foreignEntity:entityData.getForeignEntityOne()) {
//					
//					/* 一对一防止SQL查询命名冲突切入元素 Begin */
//					Element sqlForeignEntityColumnXML = DocumentHelper.createElement("sql");
//					sqlForeignEntityColumnXML.addAttribute("id", foreignEntity.getModuleName()+"Column");
//					String foreignEntityColumn="";
//					for(String foreignColumn:foreignEntity.getField()) {
//						if(foreignEntityColumn!="") {
//							foreignEntityColumn+=",";
//						}
//						foreignEntityColumn+=foreignEntity.getModuleName()+"."+foreignColumn;
//					}
//					sqlForeignEntityColumnXML.addText(foreignEntityColumn);
//					mapperXML.add(sqlForeignEntityColumnXML);
//					/* 一对一防止SQL查询命名冲突切入元素 End */
//					
//					
//					
//					/* select一对一查询 Begin */
//					String selectName = SQLType.initial(entityData.getModuleName())+"To"+SQLType.initial(foreignEntity.getModuleName());
//					Element selectOneToOne = DocumentHelper.createElement("select");
//					selectOneToOne.addAttribute("id","select"+selectName);
//					selectOneToOne.addAttribute("resultMap", SQLType.initial1(selectName));
//					selectOneToOne.addText("select ");
//					
//					/* 引入一对一查询防止SQL查询命名冲突元素 Begin */
//					Element entityColumns = DocumentHelper.createElement("include");
//					Element foreignEntityColumns = DocumentHelper.createElement("include");
//					entityColumns.addAttribute("refid", entityData.getModuleName()+"Column");
//					foreignEntityColumns.addAttribute("refid",foreignEntity.getModuleName()+"Column" );
//					selectOneToOne.add(entityColumns);
//					selectOneToOne.addText(",");
//					selectOneToOne.add(foreignEntityColumns);
//					/* 引入一对一查询防止SQL查询命名冲突元素 End */
//					
//					
//					/* 一对一连接查询 Begin */
//					selectOneToOne.addText(" from "+entityData.getModuleName());
//					selectOneToOne.addText(" inner join " + foreignEntity.getModuleName() + " on "+entityData.getModuleName()+"."+foreignEntity.getModuleName()+"Id = " + foreignEntity.getModuleName()+".id");
//					/* 一对一连接查询 End */
//					
//					
//					/* 一对一查询条件 Begin */
//					Element OneToOneTrim = DocumentHelper.createElement("trim");
//					OneToOneTrim.addAttribute("prefix", "where");
//					OneToOneTrim.addAttribute("prefixOverrides", "and | or");
//					OneToOneTrim.addAttribute("suffix", "");
//					
//					selectOneToOne.add(OneToOneTrim);
//					for(String entityColumn1:entityData.getField()) {
//						Element ifColumn = DocumentHelper.createElement("if");
//						ifColumn.addAttribute("test", entityData.getModuleName()+"."+entityColumn1+" != null");
//						ifColumn.addText("and "+entityData.getModuleName()+"."+entityColumn1+" = #{"+entityData.getModuleName()+"."+entityColumn1+"}");
//						selectOneToOne.add(ifColumn);
//					}
//					/* 一对一查询条件 End */
//					
//					/* select一对一查询 End */
//					
//					
//					
//					/* 一对一查询的resultMap元素 Begin */
//					Element resultMap = DocumentHelper.createElement("resultMap");
//					resultMap.addAttribute("id", SQLType.initial1(selectName));
//					resultMap.addAttribute("type", entityData.getClassName());
//					
//					
//					/* 一对一查询结果匹配元素设置 Begin */
//					for(String entityColumn1:entityData.getField()) {
//						Element columnEntity=null;
//						if(entityColumn1.equals("id")) {
//							columnEntity = DocumentHelper.createElement("id");
//							columnEntity.addAttribute("property", "id");
//							columnEntity.addAttribute("column", entityData.getModuleName()+".id");
//						}else {
//							columnEntity = DocumentHelper.createElement("result");
//							columnEntity.addAttribute("property",entityColumn1 );
//							columnEntity.addAttribute("column", entityData.getModuleName()+"."+entityColumn1);
//						}
//						resultMap.add(columnEntity);
//					}
//					/* 一对一查询结果匹配元素设置 End */
//					
//					
//					/* 一对一association元素设置 Begin */
//					Element association = DocumentHelper.createElement("association");
//					association.addAttribute("property", foreignEntity.getModuleName());
//					association.addAttribute("javaType", foreignEntity.getClassName());
//					/* 一对一查询结果匹配元素设置 Begin */
//					for(String entityForeignColumn:foreignEntity.getField()) {
//						Element columnEntity=null;
//						if(entityForeignColumn.equals("id")) {
//							columnEntity = DocumentHelper.createElement("id");
//							columnEntity.addAttribute("property", "id");
//							columnEntity.addAttribute("column", foreignEntity.getModuleName()+".id");
//						}else {
//							columnEntity = DocumentHelper.createElement("result");
//							columnEntity.addAttribute("property",entityForeignColumn );
//							columnEntity.addAttribute("column", foreignEntity.getModuleName()+"."+entityForeignColumn);
//						}
//						association.add(columnEntity);
//					}
//					/* 一对一查询结果匹配元素设置 End */
//					/* 一对一association元素设置 End */
//					
//					resultMap.add(association);
//					mapperXML.add(selectOneToOne);
//					mapperXML.add(resultMap);
//					/* 一对一查询的resultMap元素 End */
//				}
//				
//			}
//			/* 一对一 End */
//			
//			
//			
//			
//			/* 一对多 Begin */
//			if(!StringUtils.isEmpty(entityData.getForeignEntityMany())) {
//				
//				for(EntityData foreignEntity:entityData.getForeignEntityMany()) {
//					
//					
//					/* 一对多防止SQL查询命名冲突切入元素 Begin */
//					Element sqlForeignEntityColumnXML = DocumentHelper.createElement("sql");
//					sqlForeignEntityColumnXML.addAttribute("id", foreignEntity.getModuleName()+"Column");
//					String foreignEntityColumn="";
//					for(String foreignColumn:foreignEntity.getField()) {
//						if(foreignEntityColumn!="") {
//							foreignEntityColumn+=",";
//						}
//						foreignEntityColumn+=foreignEntity.getModuleName()+"."+foreignColumn;
//					}
//					sqlForeignEntityColumnXML.addText(foreignEntityColumn);
//					mapperXML.add(sqlForeignEntityColumnXML);
//					/* 一对多防止SQL查询命名冲突切入元素 End */
//					
//					
//					/* select一对一查询 Begin */
//					String selectName = SQLType.initial(entityData.getModuleName())+"To"+SQLType.initial(foreignEntity.getModuleName());
//					Element selectOneToOne = DocumentHelper.createElement("select");
//					selectOneToOne.addAttribute("id","select"+selectName);
//					selectOneToOne.addAttribute("resultMap", SQLType.initial1(selectName));
//					selectOneToOne.addText("select ");
//					
//					/* 引入一对一查询防止SQL查询命名冲突元素 Begin */
//					Element entityColumns = DocumentHelper.createElement("include");
//					Element foreignEntityColumns = DocumentHelper.createElement("include");
//					entityColumns.addAttribute("refid", entityData.getModuleName()+"Column");
//					foreignEntityColumns.addAttribute("refid",foreignEntity.getModuleName()+"Column" );
//					selectOneToOne.add(entityColumns);
//					selectOneToOne.addText(",");
//					selectOneToOne.add(foreignEntityColumns);
//					/* 引入一对一查询防止SQL查询命名冲突元素 End */
//					
//					
//					/* 一对一连接查询 Begin */
//					selectOneToOne.addText(" from "+entityData.getModuleName());
//					selectOneToOne.addText(" inner join " + foreignEntity.getModuleName() + " on "+entityData.getModuleName()+"."+foreignEntity.getModuleName()+"Id = " + foreignEntity.getModuleName()+".id");
//					/* 一对一连接查询 End */
//					
//					
//					/* 一对一查询条件 Begin */
//					Element OneToOneTrim = DocumentHelper.createElement("trim");
//					OneToOneTrim.addAttribute("prefix", "where");
//					OneToOneTrim.addAttribute("prefixOverrides", "and | or");
//					OneToOneTrim.addAttribute("suffix", "");
//					
//					selectOneToOne.add(OneToOneTrim);
//					for(String entityColumn1:entityData.getField()) {
//						Element ifColumn = DocumentHelper.createElement("if");
//						ifColumn.addAttribute("test", entityData.getModuleName()+"."+entityColumn1+" != null");
//						ifColumn.addText("and "+entityData.getModuleName()+"."+entityColumn1+" = #{"+entityData.getModuleName()+"."+entityColumn1+"}");
//						selectOneToOne.add(ifColumn);
//					}
//					/* 一对一查询条件 End */
//					
//					/* select一对一查询 End */
//					
//					
//					
//					/* 一对一查询的resultMap元素 Begin */
//					Element resultMap = DocumentHelper.createElement("resultMap");
//					resultMap.addAttribute("id", SQLType.initial1(selectName));
//					resultMap.addAttribute("type", entityData.getClassName());
//					
//					
//					/* 一对一查询结果匹配元素设置 Begin */
//					for(String entityColumn1:entityData.getField()) {
//						Element columnEntity=null;
//						if(entityColumn1.equals("id")) {
//							columnEntity = DocumentHelper.createElement("id");
//							columnEntity.addAttribute("property", "id");
//							columnEntity.addAttribute("column", entityData.getModuleName()+".id");
//						}else {
//							columnEntity = DocumentHelper.createElement("result");
//							columnEntity.addAttribute("property",entityColumn1 );
//							columnEntity.addAttribute("column", entityData.getModuleName()+"."+entityColumn1);
//						}
//						resultMap.add(columnEntity);
//					}
//					/* 一对一查询结果匹配元素设置 End */
//					
//					
//					/* 一对一association元素设置 Begin */
//					Element association = DocumentHelper.createElement("collection");
//					association.addAttribute("property", foreignEntity.getModuleName()+"s");
//					association.addAttribute("ofType", foreignEntity.getClassName());
//					/* 一对一查询结果匹配元素设置 Begin */
//					for(String entityForeignColumn:foreignEntity.getField()) {
//						Element columnEntity=null;
//						if(entityForeignColumn.equals("id")) {
//							columnEntity = DocumentHelper.createElement("id");
//							columnEntity.addAttribute("property", "id");
//							columnEntity.addAttribute("column", foreignEntity.getModuleName()+".id");
//						}else {
//							columnEntity = DocumentHelper.createElement("result");
//							columnEntity.addAttribute("property",entityForeignColumn );
//							columnEntity.addAttribute("column", foreignEntity.getModuleName()+"."+entityForeignColumn);
//						}
//						association.add(columnEntity);
//					}
//					/* 一对一查询结果匹配元素设置 End */
//					/* 一对一association元素设置 End */
//					
//					resultMap.add(association);
//					mapperXML.add(selectOneToOne);
//					mapperXML.add(resultMap);
//					/* 一对一查询的resultMap元素 End */
//					
//					
//				}
//				
//			}
//			/* 一对多 End */
			
			
			
			
			
			
			doc.add(mapperXML);
			/* 创建XML Begin */
			createFile(module,entityData,pash,doc);
			/* 创建XML End */
		}

	}
	
	
	
	private static void createFile(String module,EntityData entityData,String pash,Document doc) throws Exception {
		String [] fileURLs=((entityData.getPackageName())+"."+module+"."+entityData.getModuleName()+".mapper.").split("\\.");
		for(String url:fileURLs) {
			File file=new File(pash=(pash+"\\"+url));
			if(!file.exists()) {
				file.mkdir();
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileOutputStream(pash + "\\" + SQLType.initial(SQLType.underlineToCapital(entityData.getTableName())) + "Mapper" + ".xml"), format);
		writer.write(doc);
		writer.flush();
		writer.close();
	}
	
}
