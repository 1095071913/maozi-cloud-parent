package com.jiumao.dubbo.bean;
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
//package com.zhongshi.dubbo.bean;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.util.ReflectionUtils;
//
//import com.zhongshi.dubbo.annotation.GenericReference;
//import com.zhongshi.dubbo.generic.GenericDubbo;
//
//
///**
// * 
// * 	功能说明：注入Bean执行前方法
// * 
// *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
// *
// *	创建日期：2019-08-03 ：1:32:00
// *
// *	版权归属：蓝河团队
// *
// *	协议说明：Apache2.0（ 文件顶端 ）
// *
// */
//
//public class GenericReferenceFieldCallback implements ReflectionUtils.FieldCallback {
//	private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
//	private ConfigurableListableBeanFactory configurableBeanFactory;
//	private Object bean;
//	private DiscoveryClient discoveryClient;
//
//	public GenericReferenceFieldCallback(ConfigurableListableBeanFactory bf, Object bean,DiscoveryClient discoveryClient) {
//		configurableBeanFactory = bf;
//		this.bean = bean;
//		this.discoveryClient=discoveryClient;
//	}
//
//	@Override
//	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
//		if (!field.isAnnotationPresent(GenericReference.class)) {
//			return;
//		}
//		ReflectionUtils.makeAccessible(field);
//		Class<?> generic = field.getType();
//		String remote = field.getDeclaredAnnotation(GenericReference.class).remote();
//		String interfaceName = field.getDeclaredAnnotation(GenericReference.class).interfaceName();
//		Object beanInstance = getBeanInstance(GenericDubbo.class.getName(), generic);
//		ReflectionUtils.setField(ReflectionUtils.findField(GenericDubbo.class, "remote"), beanInstance, remote);
//		ReflectionUtils.setField(ReflectionUtils.findField(GenericDubbo.class, "interfaceName"), beanInstance,interfaceName);
//		field.set(bean, beanInstance);
//	}
//
//	public Object getBeanInstance(String beanName, Class<?> genericClass) {
//		Object daoInstance = null;
//		if (!configurableBeanFactory.containsBean(beanName)) {
//			Object toRegister = null;
//			try {
//				Constructor<?> ctr = genericClass.getConstructor();
//				toRegister = ctr.newInstance();
//			} catch (Exception e) {   
//				throw new RuntimeException(e);
//			}
//			daoInstance = configurableBeanFactory.initializeBean(toRegister, beanName);
//			// 以下两行是确保注入对象保持单例
//			configurableBeanFactory.autowireBeanProperties(daoInstance, AUTOWIRE_MODE, true);
//			configurableBeanFactory.registerSingleton(beanName, daoInstance);
//		} else {
//			daoInstance = configurableBeanFactory.getBean(beanName);
//		}
//		return daoInstance;
//	}
//}