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

package com.maozi.generate.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.maozi.generate.code.entity.DataSourceConfig;
import com.maozi.generate.code.entity.EntityData;
import com.maozi.generate.code.entity.TableData;
import com.maozi.generate.code.parent.GenerateParentPom;
import com.maozi.generate.code.parent.dto.GenerateDtoPom;
import com.maozi.generate.code.parent.enums.GenerateEnumPom;
import com.maozi.generate.code.parent.rest.GenerateRestPom;
import com.maozi.generate.code.parent.rpc.GenerateRpcPom;
import com.maozi.generate.code.parent.service.GenerateEntity;
import com.maozi.generate.code.parent.service.GenerateMapper;
import com.maozi.generate.code.parent.service.GenerateMapperXML;
import com.maozi.generate.code.parent.service.GenerateProperties;
import com.maozi.generate.code.parent.service.GenerateRun;
import com.maozi.generate.code.parent.service.GenerateService;
import com.maozi.generate.code.parent.service.GenerateServiceImpl;
import com.maozi.generate.code.parent.service.GenerateServicePom;
import com.maozi.generate.code.parent.vo.GenerateVoPom;
import com.maozi.generate.code.tool.SQLType;

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

public class GenerateCodeRun {
	
	public static BufferedReader  bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	
	public static String userReaderNullDefault(String defaultStr) throws IOException {
		
		String userRead = bufferedReader.readLine();
		
		return StringUtils.isEmpty(userRead) ? defaultStr:userRead;
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.print("请输入模块名字（users）：");
		String mobule = userReaderNullDefault("test");
		
		System.out.print("请输入生成地址（D:\\project\\maozi-cloud-parent\\maozi-cloud-common）：");
		String pash = userReaderNullDefault("D:\\project\\maozi-cloud-parent\\maozi-cloud-common");
		
		System.out.print("请输入是否依赖数据库（yes/no）：");
		Boolean db = userReaderNullDefault("no").equals("yes");
		
		if(db) {
			
			System.out.print("请输入数据库地址如 （默认 localhost:3306）：");
			DataSourceConfig.JDBCURL = userReaderNullDefault("localhost:3306")+"/";
			
			System.out.print("请输入数据库用户名（默认 root）：");
			DataSourceConfig.USER = userReaderNullDefault("root");
			
			System.out.print("请输入数据库密码（默认 password）：");
			DataSourceConfig.PASSWORD = userReaderNullDefault("password");
			
			System.out.print("请输入数据库名（默认 maozi-cloud-"+mobule+"-localhost-db）：");
			DataSourceConfig.JDBCURL += userReaderNullDefault("maozi-cloud-"+mobule+"-localhost-db");
			
		}
		
		Map<String,List<TableData>> moduleTableNameMap = new HashMap<String, List<TableData>>(){{
		
			for(;;) {
				
				System.out.print("请输入子模块名（输入break结束）：");
				String childModule = bufferedReader.readLine();
				if(childModule.equals("break")) {
					break;
				}
				
				put(childModule, new ArrayList<TableData>() {{
					
					if(db) {
						
						for(;;) {
							
							System.out.print("请输入表名（输入break结束）：");
							String tableName = bufferedReader.readLine();
							if(tableName.equals("break")) {
								break;
							}
							
							System.out.print("请输入表名实体映射过滤前缀：");
							String prefixFilter = bufferedReader.readLine();
							
							add(new TableData(tableName,prefixFilter));
							
						}
						
					}
					
				}});
				
			}
			
		}};
		
		generateCode(mobule,moduleTableNameMap,"com.maozi",pash,db);
		
	}

	
	
	
	
	private static void generateCode(String module,Map<String,List<TableData>> moduleTableNameMap,String packageName,String pash,Boolean db) throws Exception {
		
		System.out.println("正在生成中   。。。。。。");
		
		pash += "\\maozi-cloud-"+module;
		String voPash = pash+"\\maozi-cloud-"+module+"-vo";
		String dtoPash = pash+"\\maozi-cloud-"+module+"-dto";
		String enumPash = pash+"\\maozi-cloud-"+module+"-enum";
		String servicePash = pash+"\\maozi-cloud-service-"+module;
		String rpcPash = pash+"\\maozi-cloud-service-rpc-api-"+module;
		String restPash = pash+"\\maozi-cloud-service-rest-api-"+module;
		
		
		GenerateParentPom.generate(module, pash);
		GenerateVoPom.generate(module, voPash);
		GenerateDtoPom.generate(module, dtoPash);
		GenerateEnumPom.generate(module, enumPash);
		GenerateRpcPom.generate(module, rpcPash);
		GenerateRestPom.generate(module, restPash);
		GenerateServicePom.generate(module, servicePash,db);
		
		
		
		GenerateRun.generate(module,packageName, servicePash+ "\\src\\main\\java",db);
		GenerateProperties.generate(module, servicePash+ "\\src\\main\\resources",db);
		
		if(db) {
			
			List<EntityData> entityData = GenerateEntity.generate(module,moduleTableNameMap, packageName, servicePash+ "\\src\\main\\java");
			GenerateMapper.generate(module,entityData, servicePash+ "\\src\\main\\java");
			GenerateMapperXML.generate(module,entityData, servicePash+ "\\src\\main\\resources");
			GenerateService.generate(module,entityData, servicePash+ "\\src\\main\\java");
			GenerateServiceImpl.generate(module,entityData, servicePash+ "\\src\\main\\java");
			
		}else {
			List<EntityData> entityData = new ArrayList<EntityData>();
			for(String childModule:moduleTableNameMap.keySet()) {
				entityData.add(new EntityData(childModule, null, null, childModule, packageName));
			}
			GenerateService.generate(module,entityData, servicePash+ "\\src\\main\\java");
			GenerateServiceImpl.generate(module,entityData, servicePash+ "\\src\\main\\java");
			
		}
		
		
		
		for(String childModule : moduleTableNameMap.keySet()) {
			
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\utils", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\config", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\properties", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\impl\\rpc\\v1", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\impl\\rpc\\v2", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\impl\\rest\\v1\\app", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\impl\\rest\\v1\\pc", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\impl\\rest\\v2\\app", null,null);
			SQLType.fileCreate(servicePash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\impl\\rest\\v2\\pc", null,null);
			
			
			SQLType.fileCreate(restPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\rest\\v1\\app", null,null);
			SQLType.fileCreate(restPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\rest\\v1\\pc", null,null);
			SQLType.fileCreate(restPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\rest\\v2\\app", null,null);
			SQLType.fileCreate(restPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\rest\\v2\\pc", null,null);
			
			SQLType.fileCreate(rpcPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\rpc\\v1", null,null);
			SQLType.fileCreate(rpcPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\api\\rpc\\v2", null,null);
			
			SQLType.fileCreate(enumPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\enums", null,null);
			
			SQLType.fileCreate(dtoPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\dto\\v1\\app", null,null);
			SQLType.fileCreate(dtoPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\dto\\v1\\pc", null,null);
			SQLType.fileCreate(dtoPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\dto\\v2\\app", null,null);
			SQLType.fileCreate(dtoPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\dto\\v2\\pc", null,null);
			
			SQLType.fileCreate(voPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\vo\\v1\\app", null,null);
			SQLType.fileCreate(voPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\vo\\v1\\pc", null,null);
			SQLType.fileCreate(voPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\vo\\v2\\app", null,null);
			SQLType.fileCreate(voPash+"\\src\\main\\java\\com\\maozi\\"+module+"\\"+childModule+"\\vo\\v2\\pc", null,null);
			
		}
		
		
		Runtime runtime = Runtime.getRuntime();
		
		runtime.exec("cmd /c start explorer "+pash);

		System.out.println("代码生成完成   。。。。。");
		
	}

}
