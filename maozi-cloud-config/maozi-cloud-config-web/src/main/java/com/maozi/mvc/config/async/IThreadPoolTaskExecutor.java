package com.maozi.mvc.config.async;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.maozi.common.BaseCommon;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class IThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
 
    @Override
    public void execute(Runnable task) {

        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newCachedThreadPool());

        executorService.execute(() -> {
            
            try {task.run();} finally {
            	
                try {BaseCommon.clearContext();} catch (Exception e) {
                    BaseCommon.throwSystemError(e);
                }
                
            }
            
        });
        
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {

        ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newCachedThreadPool());
        
        return executorService.submit(() -> {
            
            try {return task.call();} finally {
            	
                try {BaseCommon.clearContext();} catch (Exception e) {
                	BaseCommon.throwSystemError(e);
                }
                
            }
            
        });
        
    }
    
}