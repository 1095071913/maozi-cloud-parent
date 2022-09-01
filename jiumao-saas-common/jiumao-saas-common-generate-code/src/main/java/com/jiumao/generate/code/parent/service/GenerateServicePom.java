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

package com.jiumao.generate.code.parent.service;

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

public class GenerateServicePom {
	
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
				"	<artifactId>jiumao-saas-service-"+module+"</artifactId>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<dependencies>\r\n" + 
				"	\r\n" + 
				"\r\n" + 
				"		<!-- "+SQLType.initial(module)+" Begin -->\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-service-rest-api-"+module+"</artifactId>\r\n" + 
				"			<version>${jiumao-saas-service-rest-api-"+module+".version}</version>\r\n" + 
				"		</dependency>\r\n" + 
				"\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-service-rpc-api-"+module+"</artifactId>\r\n" + 
				"			<version>${jiumao-saas-service-rpc-api-"+module+".version}</version>\r\n" + 
				"		</dependency>\r\n" + 
				"		<!-- "+SQLType.initial(module)+" End -->\r\n" + 
				" \r\n" + 
				" 		\r\n" + 
				"\r\n" + 
				"		<!-- jiumao-saas-cloud Begin -->\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-do</artifactId>\r\n" + 
				"			<version>${jiumao-saas-do.version}</version>\r\n" + 
				"			<type>pom</type>\r\n" + 
				"		</dependency>\r\n" + 
				"\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-service-impl</artifactId>\r\n" + 
				"			<version>${jiumao-saas-service-impl.version}</version>\r\n" + 
				"			<type>pom</type>\r\n" + 
				"		</dependency>\r\n" + 
				"\r\n" + 
				"		<dependency>\r\n" + 
				"			<groupId>com.jiumao</groupId>\r\n" + 
				"			<artifactId>jiumao-saas-service-base-impl</artifactId>\r\n" + 
				"			<version>${jiumao-saas-service-base-impl.version}</version>\r\n" + 
				"		</dependency>\r\n"+
				"		<!-- jiumao-saas-cloud End -->\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	</dependencies>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	<!-- boot打包 Begin -->\r\n" + 
				"	<build>\r\n" + 
				"		<finalName>${parent.artifactId}</finalName>\r\n" + 
				"		<plugins>\r\n" + 
				"			<plugin>\r\n" + 
				"				<groupId>org.springframework.boot</groupId>\r\n" + 
				"				<artifactId>spring-boot-maven-plugin</artifactId>\r\n" + 
				"				<version>2.1.5.RELEASE</version>\r\n" + 
				"				<configuration>\r\n" + 
				"					<mainClass>${application-run}</mainClass>\r\n" + 
				"				</configuration>\r\n" + 
				"				<executions>\r\n" + 
				"					<execution>\r\n" + 
				"						<goals>\r\n" + 
				"							<goal>repackage</goal>\r\n" + 
				"						</goals>\r\n" + 
				"					</execution>\r\n" + 
				"				</executions>\r\n" + 
				"				<dependencies>\r\n" + 
				"					<dependency>\r\n" + 
				"						<groupId>org.springframework</groupId>\r\n" + 
				"						<artifactId>springloaded</artifactId>\r\n" + 
				"						<version>1.2.6.RELEASE</version>\r\n" + 
				"					</dependency>\r\n" + 
				"				</dependencies>\r\n" + 
				"			</plugin>\r\n" + 
				"			<plugin>\r\n" + 
				"				<groupId>org.apache.maven.plugins</groupId>\r\n" + 
				"				<artifactId>maven-war-plugin</artifactId>\r\n" + 
				"				<configuration>\r\n" + 
				"					<failOnMissingWebXml>false</failOnMissingWebXml>\r\n" + 
				"				</configuration>\r\n" + 
				"			</plugin>\r\n" + 
				"		</plugins>\r\n" + 
				"	</build>\r\n" + 
				"	<!-- boot打包 End -->\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"</project>";
		
		SQLType.fileCreate(pash, "pom", new StringBuilder(context),"xml");
		
	}

}
