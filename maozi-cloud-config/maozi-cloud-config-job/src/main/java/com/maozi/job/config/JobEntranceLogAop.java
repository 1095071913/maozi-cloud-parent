package com.maozi.job.config;

import com.maozi.base.enums.EnvironmentType;
import com.maozi.base.enums.LogCommonType;
import com.maozi.common.BaseCommon;
import com.maozi.utils.constant.LogTag;
import com.maozi.utils.context.ApplicationEnvironmentContext;
import com.maozi.utils.context.ApplicationLinkContext;
import com.xxl.job.core.context.XxlJobHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1 )
public class JobEntranceLogAop extends BaseCommon {

    private final String POINT = "@annotation(com.xxl.job.core.handler.annotation.XxlJob)";

    @Around(POINT)
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        boolean error = false;

        long startTime = System.currentTimeMillis();

        String arg = XxlJobHelper.getJobParam();

        functionParam(arg);

        ApplicationLinkContext.VERSIONS.set(ApplicationEnvironmentContext.VERSION);

        Map<String, String> logs = new LinkedHashMap<>();

        logs.put(LogTag.TYPE, LogCommonType.JOB.getDesc());
        logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        Boolean isNotProd = notEnvironment(EnvironmentType.PROD);
        if(isNotProd){
            logs.put(LogTag.PARAM, arg);
        }

        try {proceedingJoinPoint.proceed();}catch (Exception e){

            error = true;

            String stackTrace = getStackTrace(e);

            functionError(stackTrace);

            log.error(stackTrace);

            if(!isNotProd){
                logs.put(LogTag.PARAM, arg);
            }

            logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());

            StackTraceElement[] errorLines = e.getStackTrace();
            if(errorLines.length > 0) {
                logs.put(LogTag.ERROR_LINE, errorLines[0].toString());
            }

        }finally {

            StringBuilder respSql = sql.get();
            if (isNotNull(respSql)) {
                logs.put(LogTag.SQL, respSql.toString());
            }

            logs.put(LogTag.RT, (System.currentTimeMillis() - startTime) + " ms");

            log(error,logs);

            BaseCommon.clearContext();

        }

    }

}
