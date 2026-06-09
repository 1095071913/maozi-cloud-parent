package com.maozi.log.config;

import cn.hutool.core.util.StrUtil;
import com.maozi.base.enums.LogCommonType;
import com.maozi.common.LogUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.constant.LogTag;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 远程调用异常处理切面
 * <p>
 * 拦截所有 RPC 和 REST 远程调用实现方法，当调用抛出异常时，
 * 记录错误日志并返回统一的错误结果，避免异常向上层传播。
 * 仅拦截接口层（非 impl 层）的调用。
 * </p>
 *
 * @author maozi
 */
@Slf4j
@Aspect
@Component
public class RemoteInvokeErrorAop {

    /** impl 包名标识，用于区分接口调用和实现调用 */
    private static final String IMPL = "impl";

    /** RPC 接口切点表达式 */
    private final String RPC_POINT = ApplicationEnvironmentContext.PACKAGE_PREFIX + ".common.result.AbstractBaseResult " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.rpc..*.*(..)";

    /** REST 接口切点表达式 */
    private final String REST_POINT = ApplicationEnvironmentContext.PACKAGE_PREFIX + ".common.result.AbstractBaseResult " + ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.api.rest..*.*(..)";

    /** 组合切点表达式 */
    private final String POINT = "execution(" + RPC_POINT + ") || execution(" + REST_POINT + ")";

    /**
     * 环绕通知，捕获远程调用异常
     *
     * @param proceedingJoinPoint AOP 连接点
     * @return 调用结果，异常时返回错误结果
     * @throws Throwable 非接口层调用时向上抛出原始异常
     */
    @Around(POINT)
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        try {return proceedingJoinPoint.proceed();} catch (Throwable e) {

            String [] remoteInvokeClassSplit = proceedingJoinPoint.getSignature().getDeclaringTypeName().split("\\.");

            if(!IMPL.equals(remoteInvokeClassSplit[5])){

                String serviceName = remoteInvokeClassSplit[2];

                Map<String, String> logs = new LinkedHashMap<>();

                logs.put(LogTag.TYPE, LogCommonType.RPC.getDesc());
                logs.put(LogTag.SERVICE_NAME, serviceName);
                logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature().getDeclaringTypeName() + ":" + proceedingJoinPoint.getSignature().getName());
                logs.put(LogTag.PARAM, Arrays.toString(proceedingJoinPoint.getArgs()));
                logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());
                logs.put(LogTag.ERROR_LINE, e.getStackTrace()[0].toString());

                LogUtil.error(log,e);

                LogUtil.error(log,logs);

                ErrorCode errorCode = SystemErrorCode.SERVICE_RPC_ERROR;
                return ResultUtil.error(errorCode)
                        .setExceptionMessage(StrUtil.upperFirst(serviceName) + errorCode.getExceptionMessage())
                        .setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);

            }

            throw e;

        }

    }

}
