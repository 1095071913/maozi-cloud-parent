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

import com.maozi.generate.code.tool.SQLType;

/**
 * 
 * 	功能说明：生成服务启动入口
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-24 : 0:24:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class GenerateRun {

	public static void generate(String module,String packageName, String pash) throws Exception {

			StringBuilder service = new StringBuilder();
			
			service.append("package " + packageName + ";\r\n\r\n");

			service.append("public class " + SQLType.initial(module) + "Application extends BaseApplicationDB {\r\n\r\n");
			
			service.append("	public static void main(String[] args) {\r\n");
			service.append("		ApplicationRun();\r\n");
			service.append("	}\r\n");
			
			service.append("\r\n}");

			/* 创建文件 Begin */
			SQLType.fileCreate(pash+"\\"+packageName.replace(".", "\\"), SQLType.initial(module) + "Application", service);
			/* 创建文件 End */


	}
	
}
