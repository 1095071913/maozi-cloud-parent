package com.jiumao.dubbo.generic;
///*
// * Copyright 2012-2018 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * 
// */
//
//package com.zhongshi.dubbo.generic;
//
//import java.util.LinkedHashMap;
//import org.apache.dubbo.config.ReferenceConfig;
//import org.apache.dubbo.rpc.service.GenericService;
//import org.springframework.stereotype.Component;
//import com.zhongshi.factory.BaseResultFactory;
//import com.zhongshi.factory.result.AbstractBaseResult;
//
///**
// * 
// * 功能说明：Dubbo泛化调用
// * 
// * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
// *
// * 创建日期：2019-08-03 ：1:32:00
// *
// * 版权归属：蓝河团队
// *
// * 协议说明：Apache2.0（ 文件顶端 ）
// *
// */
//
//@Component
//public class GenericDubbo {
//
////	public String remote;
////  public String interfaceName;
//	public static LinkedHashMap<String, ReferenceConfig<GenericService>> referenceConfigLinkedHashMap = new LinkedHashMap();
//
//
//	public <T> AbstractBaseResult invoke(String methodName, String remote, String interfaceName, String[] paramType,Object[] paramValue) {
//		GenericService genericService = getReferenceConfig(remote, interfaceName).get();
//		try { 
//			return (AbstractBaseResult) genericService.$invoke(methodName, paramType, paramValue); 
//		} catch (Exception e) {
//			BaseResultFactory.log.error(BaseResultFactory.getStackTrace(e));
//			return BaseResultFactory.error(BaseResultFactory.baseCode(600));
//		}
//	}
//
//	private ReferenceConfig<GenericService> getReferenceConfig(String remote, String interfaceName) {
//		String key = remote + "-" + interfaceName;
//		if (GenericDubbo.referenceConfigLinkedHashMap.containsKey(key)) {
//			return GenericDubbo.referenceConfigLinkedHashMap.get(key);
//		} else {
//			ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
//			reference.setInterface(interfaceName);
//			reference.setTimeout(30000);
//			reference.setUrl(remote);
//			reference.setGeneric(true);
//			GenericDubbo.referenceConfigLinkedHashMap.put(key, reference);
//			return reference;
//		}
//	}
//	
//
//}