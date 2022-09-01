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

package com.jiumao.generate.code.parent.rest;

import com.jiumao.generate.code.tool.SQLType;

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

public class GenerateRestPom {
	
	public static void generate(String module,String pash) throws Exception {
		
		String context = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + 
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" + 
				"	<modelVersion>4.0.0</modelVersion>\r\n" + 
				"	<parent>\r\n" + 
				"		<groupId>com.jiumao</groupId>\r\n" + 
				"		<artifactId>jiumao-saas-"+module+"</artifactId>\r\n" + 
				"		<version>1.0.0-SNAPSHOT</version>\r\n" + 
				"	</parent>\r\n" + 
				"	<artifactId>jiumao-saas-service-rest-api-"+module+"</artifactId>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<dependencies>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"		<!-- "+SQLType.initial(module)+" Begin -->\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-"+module+"-vo</artifactId>\r\n" + 
				"			<version>${jiumao-saas-"+module+"-vo.version}</version>\r\n" + 
				"		</dependency>\r\n" + 
				"\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-"+module+"-dto</artifactId>\r\n" + 
				"			<version>${jiumao-saas-"+module+"-dto.version}</version>\r\n" + 
				"		</dependency>\r\n" + 
				"\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-service-rest-api</artifactId>\r\n" + 
				"			<version>${jiumao-saas-service-rest-api.version}</version>\r\n" + 
				"			<type>pom</type>\r\n" + 
				"		</dependency>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	</dependencies>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"</project>";
		
		SQLType.fileCreate(pash, "pom", new StringBuilder(context),"xml");
		
	}

}
