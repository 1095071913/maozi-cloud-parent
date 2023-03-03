package com.maozi.mybatisplus.config;

import org.apache.ibatis.logging.Log;

import com.maozi.factory.BaseResultFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MybatisPlusLog implements Log {

	public MybatisPlusLog(String clazz) {}

	public boolean isDebugEnabled() {return true;}

	public boolean isTraceEnabled() {return true;}

	public void error(String s, Throwable e) {log.error(s);}

	public void error(String s) {log.error(s); }
 
	public void debug(String s) {
		
		StringBuilder sql = BaseResultFactory.sql.get();
		
		if(BaseResultFactory.isNull(sql)) {
			
			sql = new StringBuilder();
			
			BaseResultFactory.sql.set(sql);
			
		}
		
		if(s.contains("==>  Preparing: ")) {
			s=s.replace("==>  Preparing: ",""); 
			s+="(";
			sql.append(s);
		}
		
		if(s.contains("==> Parameters: ")) {
			s=s.replace("==> Parameters: ","");
			s+=")";
			sql.append(s+",         ");    
		}   

	} 
 
	public void trace(String s) {}

	public void warn(String s) {}

	
}