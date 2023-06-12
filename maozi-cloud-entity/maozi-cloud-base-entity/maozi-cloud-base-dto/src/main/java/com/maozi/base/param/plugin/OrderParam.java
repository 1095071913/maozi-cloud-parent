package com.maozi.base.param.plugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModelProperty;

public interface OrderParam {
	
	@ApiModelProperty("排序组件 key:排序字段 value: true降序、false升序、null不做排序、空字段")
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
	
	
	public default void initOrderParam() {
		
		Map<String, Map<String, Boolean>> orderMainFieldsMap = getOrderMainFieldsMap();
		
		Map<String,List<String>> orderAscFieldsMap = Maps.newHashMap();
		
		Map<String,List<String>> orderDescFieldsMap = Maps.newHashMap();
		
		for(String key : orderMainFieldsMap.keySet()) {
			
			Map<String,Boolean> orderMainFields = orderMainFieldsMap.get(key);
			
			List<String> orderAesFields = Lists.newArrayList();
			
			List<String> orderDescFields = Lists.newArrayList();
			
			for(String orderMainField : orderMainFields.keySet()) {	
				
				Map<String, Boolean> orderFieldMap = getOrderFieldMap();
				
				Boolean orderMainFieldValue = orderFieldMap.containsKey(orderMainField) ? orderFieldMap.get(orderMainField) : orderMainFields.get(orderMainField);
				
				if(Objects.nonNull(orderMainFieldValue)) {
					
					String underlineCase = StrUtil.toUnderlineCase(key);
					
					String field = StringUtils.isEmpty(key) ? underlineCase : underlineCase+"."+StrUtil.toUnderlineCase(orderMainField);
					
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
