package com.xxl.job.core.handler.impl;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;
import com.zhongshi.factory.BaseResultFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodJobHandler extends IJobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;

        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void execute() throws Exception {
        
    	Long beginTime = System.currentTimeMillis();
    	
    	Boolean isError=false; 
    	
    	functionParam(new String(XxlJobHelper.getJobParam()));
    	
    	Map<String, String> logs = new LinkedHashMap<String, String>();
    	
    	logs.put("reqType", "job"); 
    	 
    	logs.put("reqFunc", method.getDeclaringClass().getName()+":"+method.getName()); 
    	
    	Class<?>[] paramTypes = method.getParameterTypes();
    	 
    	try { 
    		
            if (paramTypes.length > 0) {method.invoke(target, new Object[paramTypes.length]);} 
            
            else {method.invoke(target);}
            
    	}catch (Exception e) {
    		
    		isError=true;
    		
    		String stackTrace = BaseResultFactory.getStackTrace(e);
    		
    		log.error(stackTrace);
    		
    		functionError(stackTrace);
			
			StackTraceElement stackTraceElement = stackTraceElement = e.getStackTrace()[0];
            
            logs.put("errorDesc", e.getLocalizedMessage());
            logs.put("errorLine", stackTraceElement.toString());
    		
    		throw e;
    		
		}finally {
			
			logs.put("respSql", BaseResultFactory.sql.get());
			
			logs.put("respTime",System.currentTimeMillis()-beginTime + " ms");
			
			StringBuilder appendLog = BaseResultFactory.appendLog(logs);
			
			if(isError) {
				log.error(appendLog.toString());
			}else { 
				log.info(appendLog.toString()); 
			}  
			
		}
    }

    @Override
    public void init() throws Exception {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws Exception {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
    
    @Trace
    @Tags({@Tag(key = "错误值", value = "arg[0]")})
    public void functionError(Object errorMessage) {}
	
	@Trace(operationName = "入参值")
    @Tags({@Tag(key = "入参值", value = "arg[0]")})
    public void functionParam(Object param) { }
    
}
