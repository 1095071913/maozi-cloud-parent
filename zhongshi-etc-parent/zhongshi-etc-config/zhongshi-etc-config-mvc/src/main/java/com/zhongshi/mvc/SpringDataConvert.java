
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

package com.zhongshi.mvc;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.zhongshi.applicationcontext.ApplicationContextHolder;


/**
 * 
 * 	功能说明：HTTP MVC自定义Convert添加配置
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


@Configuration 
public class SpringDataConvert {
 
	private String scanPackage = "com.zhongshi";
	
	@Autowired
	private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
	
	@PostConstruct
	public void addConversionConfig() throws URISyntaxException {
		ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer) requestMappingHandlerAdapter.getWebBindingInitializer();
		
		if (initializer.getConversionService() != null) {
			GenericConversionService genericConversionService = (GenericConversionService) initializer.getConversionService();
			scanPackage(this.scanPackage,genericConversionService);
		}
	}
	
	
	private void scanPackage(String scanPackage,GenericConversionService genericConversionService) throws URISyntaxException { 
//		URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
//		File file = new File(url.toURI()); 
//		file.listFiles((childFiles) -> {
//			if (childFiles.isDirectory()) {
//				try {
//					scanPackage(scanPackage + "." + childFiles.getName(),genericConversionService);
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				}
//			} else {
//				if (childFiles.getName().endsWith(".class")) {
//					String classPath = scanPackage + "." + childFiles.getName().replaceAll("\\.class", "");
//					if(classPath.lastIndexOf("convert")!=-1) {
//						try {
//							Class<?> classConvert = Class.forName(classPath);
//							genericConversionService.addConverter((Converter)classConvert.newInstance());
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//			return false;
//		});
	}
	
}