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

package com.zhongshi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import com.zhongshi.entity.DataSourceConfig;
import com.zhongshi.entity.EntityData;
import com.zhongshi.generate.GenerateEntity;
import com.zhongshi.generate.GenerateMapper;
import com.zhongshi.generate.GenerateMapperXML;
import com.zhongshi.generate.GenerateProperties;
import com.zhongshi.generate.GenerateRun;
import com.zhongshi.generate.GenerateService;
import com.zhongshi.generate.GenerateServiceImpl;

/**
 * 
 * 	功能说明：代码生成
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-20 ：8:05:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */

public class GenerateCode {

	public static void main(String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("请输入数据库地址如 ( cdb-qub9xild.gz.tencentcdb.com:10021/user)：");
		DataSourceConfig.JDBCURL = bufferedReader.readLine();
		
		System.out.print("请输入数据库用户名：");
		DataSourceConfig.USER = bufferedReader.readLine();
		
		System.out.print("请输入数据库密码：");
		DataSourceConfig.PASSWORD = bufferedReader.readLine();
		
		System.out.print("请输入模块名字（数据库名）：");
		String mobule = bufferedReader.readLine();
		
		System.out.print("请输入包名：");
		String packageName = bufferedReader.readLine() + "." + mobule;
		
		System.out.print("请输入生成地址：");
		String pash = bufferedReader.readLine();
		
		generateCode(mobule,packageName,pash);
		
	}

	
	
	
	
	private static void generateCode(String mobule,String packageName,String pash) throws Exception {
		System.out.println("正在生成中   。。。。。。");

		/* 生成Entity Begin */
		List<EntityData> entityData = GenerateEntity.generate(mobule, packageName, pash+ "\\src\\main\\java");
		/* 生成Entity End */

		
		/* 生成Run Begin */
		GenerateRun.generate(mobule,packageName, pash+ "\\src\\main\\java");
		/* 生成Run End */
		
		
		/* 生成properties Begin */
		GenerateProperties.generate(mobule, pash+ "\\src\\main\\resources");
		/* 生成properties End */
		
		
		/* 生成Mapper Begin */
		GenerateMapper.generate(entityData, pash+ "\\src\\main\\java");
		/* 生成Mapper End */
		
		
		/* 生成MapperXML Begin */
		GenerateMapperXML.generate(entityData, pash+ "\\src\\main\\resources");
		/* 生成MapperXML End */
		

		/* 生成Service Begin */
		GenerateService.generate(entityData, pash+ "\\src\\main\\java");
		/* 生成Service End */
		

		/* 生成ServiceImpl Begin */
		GenerateServiceImpl.generate(entityData, pash+ "\\src\\main\\java");
		/* 生成ServiceImpl End */

		System.out.println("代码生成完成   。。。。。");
	}

}
