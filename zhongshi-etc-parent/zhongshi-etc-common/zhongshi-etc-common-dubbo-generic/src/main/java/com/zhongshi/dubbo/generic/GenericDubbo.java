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

package com.zhongshi.dubbo.generic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

import com.zhongshi.base.AbstractBaseDomain;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;

/**
 * 
 * 	功能说明：Dubbo泛化调用
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

public class GenericDubbo{
    
	public String remote;
    public String interfaceName;
    public static LinkedHashMap<String, ReferenceConfig<GenericService>> referenceConfigLinkedHashMap=new LinkedHashMap();

    //单个参数
    public AbstractBaseResult invoke(String methodName,Object ... paramValue){
        GenericService genericService = getReferenceConfig().get();
        Map<String,String> data= (HashMap<String,String>)genericService.$invoke(methodName, new String[]{AbstractBaseDomain.class.getName()}, paramValue);
        return BaseResultFactory.success(data.toString());
    }
    //多个参数       
    public Object invoke(String methodName, String[] paramType, Object[] paramValue){
        GenericService genericService = getReferenceConfig().get();
        return genericService.$invoke(methodName, paramType, paramValue);
    }
    private ReferenceConfig<GenericService> getReferenceConfig(){
        String key = remote+"-"+interfaceName;
        if(GenericDubbo.referenceConfigLinkedHashMap.containsKey(key)){
            return GenericDubbo.referenceConfigLinkedHashMap.get(key);
        }else{
            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            //reference.setApplication(new ApplicationConfig("dubbo"));
            reference.setInterface(interfaceName);
            reference.setUrl(remote);
            reference.setGeneric(true); 
            GenericDubbo.referenceConfigLinkedHashMap.put(key, reference);
            return reference;
        }
    }
    
}