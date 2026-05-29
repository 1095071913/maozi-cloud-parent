package com.maozi.db.config;

import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.Log;

@Slf4j
public class MybatisPlusLog implements Log {

	public MybatisPlusLog(String clazz) {}

	public boolean isDebugEnabled() {return true;}

	public boolean isTraceEnabled() {return true;}

	public void error(String s, Throwable e) {log.error(s);}

	public void error(String s) {log.error(s); }
 
	public void debug(String s) {

		if(!ApplicationEnvironmentContext.IS_RUNNING){
			return;
		}
		
		StringBuilder sqlLog = LogUtil.sqlLog.get();
		
		if(ObjectUtil.isNullEmpty(sqlLog)) {

			sqlLog = new StringBuilder();

			LogUtil.sqlLog.set(sqlLog);
			
		}
		
		if(s.contains("==>  Preparing: ")) {
			
			s=s.replace("==>  Preparing: ","");

			sqlLog.append(s);
			
		}
		
		if(s.contains("==> Parameters: ")) {
			
			s=s.replace("==> Parameters: ","");
				
			if(StringUtils.isNotBlank(s)) {
				
				String [] params = s.split("\\),");

                for (String param : params) {

                    param = "'" + param.substring(0, param.indexOf("(")) + "'";

                    int index = sqlLog.indexOf("?");

                    if (index != -1) {
                        sqlLog.replace(index, index + 1, param);

                    }

                }
				
			}

			sqlLog.append(";");
			
		}   

	} 
 
	public void trace(String s) {}

	public void warn(String s) {}
	
}