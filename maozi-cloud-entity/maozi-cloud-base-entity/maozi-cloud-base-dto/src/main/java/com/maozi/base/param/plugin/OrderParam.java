package com.maozi.base.param.plugin;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public interface OrderParam {
	
	@Schema(description = "排序组件 key:排序字段 value: true降序、false升序、null不做排序、空字段")
	Map<String,Boolean> getOrderFieldMap();
	
	@JsonIgnore
	Map<String,Map<String,Boolean>> getOrderMainFieldsMap();
	
	@JsonIgnore
	Map<String,List<String>> getOrderAscFieldsMap();
	@JsonIgnore
	void setOrderAscFieldsMap(Map<String,List<String>> orderAscFieldsMap);
	
	@JsonIgnore
	Map<String,List<String>> getOrderDescFieldsMap();
	@JsonIgnore
	void setOrderDescFieldsMap(Map<String,List<String>> orderDescFieldsMap);
	
	
	default void initOrderParam() {
		
		Map<String, Map<String, Boolean>> orderMainFieldsMap = getOrderMainFieldsMap();
		
		Map<String,List<String>> orderAscFieldsMap = CollectionUtil.newHashMap();
		
		Map<String,List<String>> orderDescFieldsMap = CollectionUtil.newHashMap();
		
		for(String key : orderMainFieldsMap.keySet()) {
			
			Map<String,Boolean> orderMainFields = orderMainFieldsMap.get(key);
			
			List<String> orderAesFields = CollectionUtil.newArrayList();
			
			List<String> orderDescFields = CollectionUtil.newArrayList();
			
			for(String orderMainField : orderMainFields.keySet()) {	
				
				Map<String, Boolean> orderFieldMap = getOrderFieldMap();

				if(ObjectUtil.isNullEmpty(orderFieldMap)){
					orderFieldMap = CollectionUtil.newHashMap();
				}
				
				Boolean orderMainFieldValue = orderFieldMap.containsKey(orderMainField) ? orderFieldMap.get(orderMainField) : orderMainFields.get(orderMainField);
				
				if(ObjectUtil.isNotNullEmpty(orderMainFieldValue)) {
					
					String underlineCase = StrUtil.toUnderlineCase(key);
					
					String field = StringUtils.isEmpty(key) ? underlineCase : underlineCase + "." + StrUtil.toUnderlineCase(orderMainField);
					
					if(orderMainFieldValue) {
						orderDescFields.add(field);
					}else {
						orderAesFields.add(field);
					}
					
				}
				
			}
			
			orderAscFieldsMap.put(key, orderAesFields);
			
			orderDescFieldsMap.put(key, orderDescFields);
			
		}
		
		setOrderAscFieldsMap(orderAscFieldsMap);
		
		setOrderDescFieldsMap(orderDescFieldsMap);
		
	}
	
}
