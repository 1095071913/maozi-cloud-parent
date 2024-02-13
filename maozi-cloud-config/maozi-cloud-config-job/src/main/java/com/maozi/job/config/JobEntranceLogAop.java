package com.maozi.job.config;

import com.maozi.base.enums.EnvironmentType;
import com.maozi.common.BaseCommon;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import com.maozi.utils.context.ApplicationLinkContext;
import com.xxl.job.core.context.XxlJobHelper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1 )
public class JobEntranceLogAop extends BaseCommon {

    @Around("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Boolean error = false;

        Long startTime = System.currentTimeMillis();

        String arg = XxlJobHelper.getJobParam();

        functionParam(arg);

        ApplicationLinkContext.VERSIONS.set(ApplicationEnvironmentContext.version);

        Map<String, String> logs = new LinkedHashMap<String, String>();

        logs.put("Type", "Job");
        logs.put("Function", proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        if(notEnvironment(EnvironmentType.production)){
            logs.put("Param", arg);
        }

        try {proceedingJoinPoint.proceed();}catch (Exception e){

            error = true;

            String stackTrace = getStackTrace(e);

            functionError(stackTrace);

            log.error(stackTrace);

            logs.put("ErrorParam", arg);

            logs.put("ErrorDesc", e.getLocalizedMessage());

            StackTraceElement[] errorLines = e.getStackTrace();
            if(errorLines.length > 0) {
                logs.put("ErrorLine", errorLines[0].toString());
            }

        }finally {

            StringBuilder respSql = sql.get();
            if (isNotNull(respSql)) {
                logs.put("SQL", respSql.toString());
            }

            logs.put("RT", (System.currentTimeMillis() - startTime) + " ms");

            log(error,logs);

            BaseCommon.clearContext();

        }

    }

}
