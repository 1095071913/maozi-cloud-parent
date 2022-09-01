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

package com.jiumao.generate.code.parent;

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

public class GenerateParentPom {
	
	public static void generate(String module,String pash) throws Exception {
		
		String context = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + 
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n" + 
				"	<modelVersion>4.0.0</modelVersion>\r\n" + 
				"	<parent>\r\n" + 
				"		<groupId>com.jiumao</groupId>\r\n" + 
				"		<artifactId>jiumao-saas-parent</artifactId>\r\n" + 
				"		<version>1.5.0-SNAPSHOT</version>\r\n" + 
				"	</parent>\r\n" + 
				"	<artifactId>jiumao-saas-"+module+"</artifactId>\r\n" + 
				"	<version>1.0.0-SNAPSHOT</version>\r\n" + 
				"	<packaging>pom</packaging>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<properties>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"		<application-run>com.jiumao."+SQLType.initial(module)+"Application</application-run>\r\n" + 
				"\r\n" + 
				"		<!-- "+SQLType.initial(module)+" Begin -->\r\n" + 
				"		<jiumao-saas-"+module+"-vo.version>1.0.0-SNAPSHOT</jiumao-saas-"+module+"-vo.version>\r\n" + 
				"		<jiumao-saas-"+module+"-do.version>1.0.0-SNAPSHOT</jiumao-saas-"+module+"-do.version>\r\n" + 
				"		<jiumao-saas-"+module+"-dto.version>1.0.0-SNAPSHOT</jiumao-saas-"+module+"-dto.version>\r\n" + 
				"		<jiumao-saas-"+module+"-enum.version>1.0.0-SNAPSHOT</jiumao-saas-"+module+"-enum.version>\r\n" + 
				"		<jiumao-saas-service-rpc-api-"+module+".version>1.0.0-SNAPSHOT</jiumao-saas-service-rpc-api-"+module+".version>\r\n" + 
				"		<jiumao-saas-service-rest-api-"+module+".version>1.0.0-SNAPSHOT</jiumao-saas-service-rest-api-"+module+".version>\r\n" + 
				"		<!-- "+SQLType.initial(module)+" End -->\r\n" + 
				"		\r\n" + 
				"		\r\n" + 
				"		<jiumao-saas-sso-user.version>1.0.0-SNAPSHOT</jiumao-saas-sso-user.version>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	</properties>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<distributionManagement>\r\n" + 
				"		<repository>\r\n" + 
				"			<id>maven-releases</id>\r\n" + 
				"			<name>Nexus Release Repository</name>\r\n" + 
				"			<url>http://175.178.52.106:8081/repository/maven-releases/</url>\r\n" + 
				"		</repository>\r\n" + 
				"		<snapshotRepository>\r\n" + 
				"			<id>maven-snapshots</id>\r\n" + 
				"			<name>Nexus Snapshot Repository</name>\r\n" + 
				"			<url>http://175.178.52.106:8081/repository/maven-snapshots/</url>\r\n" + 
				"		</snapshotRepository>\r\n" + 
				"	</distributionManagement>\r\n" + 
				"	<repositories>\r\n" + 
				"		<repository>\r\n" + 
				"			<id>maven-public</id>\r\n" + 
				"			<name>Nexus Repository</name>\r\n" + 
				"			<url>http://175.178.52.106:8081/repository/maven-public/</url>\r\n" + 
				"			<snapshots>\r\n" + 
				"				<enabled>true</enabled>\r\n" + 
				"			</snapshots>\r\n" + 
				"			<releases>\r\n" + 
				"				<enabled>true</enabled>\r\n" + 
				"			</releases>\r\n" + 
				"		</repository>\r\n" + 
				"	</repositories>\r\n" + 
				"	<pluginRepositories>\r\n" + 
				"		<pluginRepository>\r\n" + 
				"			<id>maven-public</id>\r\n" + 
				"			<name>Nexus Plugin Repository</name>\r\n" + 
				"			<url>http://175.178.52.106:8081/repository/maven-public/</url>\r\n" + 
				"			<snapshots>\r\n" + 
				"				<enabled>true</enabled>\r\n" + 
				"			</snapshots>\r\n" + 
				"			<releases>\r\n" + 
				"				<enabled>true</enabled>\r\n" + 
				"			</releases>\r\n" + 
				"		</pluginRepository>\r\n" + 
				"	</pluginRepositories>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<modules>\r\n" + 
				"		<module>jiumao-saas-"+module+"-vo</module>\r\n" + 
				"		<module>jiumao-saas-"+module+"-dto</module>\r\n" + 
				"		<module>jiumao-saas-service-rpc-api-"+module+"</module>\r\n" + 
				"		<module>jiumao-saas-service-rest-api-"+module+"</module>\r\n" + 
				"		<module>jiumao-saas-service-"+module+"</module>\r\n" + 
				"		<module>jiumao-saas-"+module+"-enum</module>\r\n" + 
				"	</modules>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"</project>";
		
		
		SQLType.fileCreate(pash, "pom", new StringBuilder(context),"xml");
		
	}

}
