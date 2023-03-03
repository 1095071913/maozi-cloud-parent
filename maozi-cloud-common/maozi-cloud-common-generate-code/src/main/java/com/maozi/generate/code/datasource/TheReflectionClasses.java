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

package com.maozi.generate.code.datasource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 
 * 	功能说明：ORM匹配元素
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-20 12:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


public class TheReflectionClasses {
	
	public static List ToObjectList(Object o, Class c, List<Map<String, Object>> resultSetList) throws Exception {
		List resultList = new ArrayList();
		Field[] fields = c.getDeclaredFields();
		for (final Map<String, Object> map : resultSetList) {
			if(o==null){
				o=c.newInstance();
			}
			// ������������
			for (Field f : fields) {
				System.out.println(f.getName()+"\t"+f.getType());
				System.out.println("��Ӧ");
				System.out.println(map.keySet()+"\t"+map.get(f.getName()).getClass().getTypeName());//���������Ա��Ժ󿴴���
				String fieldName = f.getName();
				String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				Method method = c.getMethod(methodName, new Class[] { f.getType() });
				method.invoke(o, map.get(f.getName()));
				map.remove(f.getName());
			}
			// ������������
			for (Class a = c.getSuperclass(); a != Object.class; a = a.getSuperclass()) {
				ToObjectList(o, a, new ArrayList<Map<String, Object>>() {
					{
						add(map);
					}
				});
			}
			resultList.add(o);
			o=null;
		}
		
		return resultList;
	}

}
