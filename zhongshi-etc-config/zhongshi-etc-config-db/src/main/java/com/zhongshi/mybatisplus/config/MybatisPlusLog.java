package com.zhongshi.mybatisplus.config;

import org.apache.ibatis.logging.Log;

import com.zhongshi.factory.BaseResultFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MybatisPlusLog implements Log {

	public MybatisPlusLog(String clazz) {}

	public boolean isDebugEnabled() {return true;}

	public boolean isTraceEnabled() {return true;}

	public void error(String s, Throwable e) {log.error(s);}

	public void error(String s) {log.error(s); }
 
	public void debug(String s) {
		
		
		if(s.contains("==>  Preparing: ")) {
			s=s.replace("==>  Preparing: ",""); 
			s+="(";
			BaseResultFactory.sql.set(BaseResultFactory.isNull(BaseResultFactory.sql.get())?s:BaseResultFactory.sql.get()+s);
		}
		
		if(s.contains("==> Parameters: ")) {
			s=s.replace("==> Parameters: ","");
			s+=")";
			BaseResultFactory.sql.set(BaseResultFactory.sql.get()+s+",         ");    
		}   

	} 
 
	public void trace(String s) {}

	public void warn(String s) {}

	
}