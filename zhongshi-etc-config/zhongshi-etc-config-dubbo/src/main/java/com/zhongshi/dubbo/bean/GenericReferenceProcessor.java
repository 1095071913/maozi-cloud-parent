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
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ReflectionUtils;
//
///**
// * 
// * 	功能说明：Bean创建生命周期
// * 
// *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
// *
// *	创建日期：2019-09-06 : 20:58:00
// *
// *	版权归属：蓝河团队
// *
// *	协议说明：Apache2.0（ 文件顶端 ）
// *
// */
//
//@Component
//public class GenericReferenceProcessor implements BeanPostProcessor {
//	
//	@Autowired
//    private ConfigurableListableBeanFactory configurableBeanFactory;
//	
//	@Autowired
//	private DiscoveryClient discoveryClient;
//
//	
//    //bean初始化方法调用前被调用
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        this.scanGenericReferenceAnnotation(bean);
//        return bean;
//    }
//    //bean初始化方法调用后被调用
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        return bean;
//    }
//    
//    protected void scanGenericReferenceAnnotation(Object bean) {
//        this.configureFieldInjection(bean);
//    }
//
//    private void configureFieldInjection(Object bean) {
//        Class<?> managedBeanClass = bean.getClass();
//        ReflectionUtils.FieldCallback fieldCallback = new GenericReferenceFieldCallback(configurableBeanFactory, bean,discoveryClient);
//        ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);
//    }
//}