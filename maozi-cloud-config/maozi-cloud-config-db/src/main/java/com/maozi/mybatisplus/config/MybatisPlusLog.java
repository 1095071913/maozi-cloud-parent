package com.maozi.mybatisplus.config;

import org.apache.ibatis.logging.Log;

import com.maozi.common.BaseCommon;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MybatisPlusLog implements Log {

	public MybatisPlusLog(String clazz) {}

	public boolean isDebugEnabled() {return true;}

	public boolean isTraceEnabled() {return true;}

	public void error(String s, Throwable e) {log.error(s);}

	public void error(String s) {log.error(s); }
 
	public void debug(String s) {
		
		StringBuilder sql = BaseCommon.sql.get();
		
		if(BaseCommon.isNull(sql)) {
			
			sql = new StringBuilder();
			
			BaseCommon.sql.set(sql);
			
		}
		
		if(s.contains("==>  Preparing: ")) {
			
			s=s.replace("==>  Preparing: ",""); 
			
			sql.append(s); 
			
		}
		
		if(s.contains("==> Parameters: ")) {
			
			s=s.replace("==> Parameters: ","");
				
			if(BaseCommon.isNotEmpty(s)) {
				
				String [] params = s.split("\\),");
				
				for(Integer i = 0 ; i < params.length ; i++) {
					
					String param = params[i];
					
					param = "'"+param.substring(0,param.indexOf("("))+"'";
					
					int index = sql.indexOf("?");
					 
					if(index != -1) {
						sql.replace(index, index+1, param);
						
					}
					
				}
				
			}
			
			sql.append(";");
			
		}   

	} 
 
	public void trace(String s) {}

	public void warn(String s) {}
	
}