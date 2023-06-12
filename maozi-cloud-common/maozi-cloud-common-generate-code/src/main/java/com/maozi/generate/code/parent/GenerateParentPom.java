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

package com.maozi.generate.code.parent;

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

public class GenerateParentPom {
	
	public static void generate(String module,String pash) throws Exception {
		
		String context = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + 
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" + 
				"	<modelVersion>4.0.0</modelVersion>\r\n" + 
				"	<parent>\r\n" + 
				"		<groupId>com.maozi</groupId>\r\n" + 
				"		<artifactId>maozi-cloud-parent</artifactId>\r\n" + 
				"		<version>1.5.0-SNAPSHOT</version>\r\n" + 
				"	</parent>\r\n" + 
				"	<artifactId>maozi-cloud-"+module+"</artifactId>\r\n" + 
				"	<version>1.0.0-SNAPSHOT</version>\r\n" + 
				"	<packaging>pom</packaging>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<properties>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"		<application-run>com.maozi."+SQLType.initial(module)+"Application</application-run>\r\n" + 
				"\r\n" + 
				"		<!-- "+SQLType.initial(module)+" Begin -->\r\n" + 
				"		<maozi-cloud-"+module+"-vo.version>1.0.0-SNAPSHOT</maozi-cloud-"+module+"-vo.version>\r\n" + 
				"		<maozi-cloud-"+module+"-dto.version>1.0.0-SNAPSHOT</maozi-cloud-"+module+"-dto.version>\r\n" + 
				"		<maozi-cloud-"+module+"-enum.version>1.0.0-SNAPSHOT</maozi-cloud-"+module+"-enum.version>\r\n" + 
				"		<maozi-cloud-service-rpc-api-"+module+".version>1.0.0-SNAPSHOT</maozi-cloud-service-rpc-api-"+module+".version>\r\n" + 
				"		<maozi-cloud-service-rest-api-"+module+".version>1.0.0-SNAPSHOT</maozi-cloud-service-rest-api-"+module+".version>\r\n" + 
				"		<!-- "+SQLType.initial(module)+" End -->\r\n" + 
				"		\r\n" + 
				"		\r\n" + 
				"		<maozi-cloud-sso-user.version>1.0.0-SNAPSHOT</maozi-cloud-sso-user.version>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	</properties>\r\n" +
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<modules>\r\n" + 
				"		<module>maozi-cloud-"+module+"-vo</module>\r\n" + 
				"		<module>maozi-cloud-"+module+"-dto</module>\r\n" + 
				"		<module>maozi-cloud-service-rpc-api-"+module+"</module>\r\n" + 
				"		<module>maozi-cloud-service-rest-api-"+module+"</module>\r\n" + 
				"		<module>maozi-cloud-service-"+module+"</module>\r\n" + 
				"		<module>maozi-cloud-"+module+"-enum</module>\r\n" + 
				"	</modules>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"</project>";
		
		
		SQLType.fileCreate(pash, "pom", new StringBuilder(context),"xml");
		
	}

}
