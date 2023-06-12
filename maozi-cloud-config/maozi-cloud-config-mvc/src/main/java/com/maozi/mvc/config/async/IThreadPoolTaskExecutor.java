package com.maozi.mvc.config.async;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.maozi.common.BaseCommon;

public class IThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
 
    @Override
    public void execute(Runnable task) {
    	
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        
        SecurityContext securityContext = SecurityContextHolder.getContext();
        
        super.execute(() -> {
        	
            if (BaseCommon.isNotNull(mdcContext)) {
                MDC.setContextMap(mdcContext);
            }
            
            if(BaseCommon.isNotNull(securityContext)) {
            	SecurityContextHolder.setContext(securityContext);
            }
            
            try {task.run();} finally {
            	
                try {BaseCommon.clear();} catch (Exception e) {
                    BaseCommon.throwSystemError(e);
                }
                
            }
            
        });
        
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {

    	Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        
        SecurityContext securityContext = SecurityContextHolder.getContext();
        
        return super.submit(() -> {
        	
        	if (BaseCommon.isNotNull(mdcContext)) {
                MDC.setContextMap(mdcContext);
            }
            
            if(BaseCommon.isNotNull(securityContext)) {
            	SecurityContextHolder.setContext(securityContext);
            }
            
            try {return task.call();} finally {
            	
                try {BaseCommon.clear();} catch (Exception e) {
                	BaseCommon.throwSystemError(e);
                }
                
            }
            
        });
        
    }
    
}