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

        // 尝试执行目标方法，如果正常返回则直接返回结果
        try {return proceedingJoinPoint.proceed();} catch (Throwable e) {

            // 解析被调用类的全限定名，按 "." 分割以提取包路径中的各段信息
            // 包路径格式约定为：com.maozi.{服务名}.{模块名}.{层级}.rpc.{类名}
            String [] remoteInvokeClassSplit = proceedingJoinPoint.getSignature().getDeclaringTypeName().split("\\.");

            // 判断是否为接口层调用（非 impl 层）
            // remoteInvokeClassSplit[5] 对应包路径中 api 下的子包名，
            // 如果是 "impl" 则表示是服务实现层的内部调用，需要继续向上抛出异常
            if(!IMPL.equals(remoteInvokeClassSplit[5])){

                // 从包路径第3段提取服务名称，例如 "com.maozi.user.xxx" 中的 "user"
                String serviceName = remoteInvokeClassSplit[2];

                // 构建结构化的错误日志信息
                Map<String, String> logs = new LinkedHashMap<>();

                logs.put(LogTag.TYPE, LogCommonType.RPC.getDesc());                     // 日志类型：RPC 远程调用
                logs.put(LogTag.SERVICE_NAME, serviceName);                              // 被调用的服务名称
                logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature()             // 被调用的方法全路径
                        .getDeclaringTypeName() + ":" + proceedingJoinPoint.getSignature().getName());
                logs.put(LogTag.PARAM, Arrays.toString(proceedingJoinPoint.getArgs()));  // 方法入参
                logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());                    // 异常描述信息
                logs.put(LogTag.ERROR_LINE, e.getStackTrace()[0].toString());            // 异常发生的代码行

                // 记录异常堆栈日志
                LogUtil.error(log,e);

                // 记录结构化的错误日志信息
                LogUtil.error(log,logs);

                // 构建统一的 RPC 调用错误响应，避免异常向上传播
                ErrorCode errorCode = SystemErrorCode.SERVICE_RPC_ERROR;
                return ResultUtil.error(errorCode)
                        .setExceptionMessage(StrUtil.upperFirst(serviceName) + errorCode.getExceptionMessage())  // 错误消息前拼接服务名
                        .setHttpCode(SystemErrorCode.SYSTEM_ERROR_DEFAULT_CODE);                                  // 设置统一的系统错误 HTTP 状态码

            }

            // impl 层的调用直接抛出原始异常，交给上层处理
            throw e;

        }

    }

}
