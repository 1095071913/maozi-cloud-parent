package com.maozi.log.utils;

import com.maozi.base.enums.LogCommonType;
import com.maozi.common.ObjectUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.constant.LogTag;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST 入口日志工具类
 * <p>
 * 构建请求入口的基础日志信息，自动判断请求类型（HTTP 或 RPC），
 * 并收集 IP 地址、请求 URL、调用函数等日志字段。
 * </p>
 *
 * @author maozi
 */
@Component
public class RestEntranceLogUtils{

    /**
     * 构建请求入口日志信息
     *
     * @param proceedingJoinPoint AOP 连接点
     * @param request HTTP 请求对象（RPC 调用时为 null）
     * @param rpcUrl RPC 调用的远程地址
     * @return 日志键值对映射
     */
    public Map<String, String> requestLog(ProceedingJoinPoint proceedingJoinPoint,HttpServletRequest request,String rpcUrl) {

        // 使用 LinkedHashMap 保证日志字段的输出顺序与插入顺序一致
        Map<String, String> logs = new LinkedHashMap<>();

        // 判断请求类型：HTTP 请求对象不为空则为 Web 请求，否则为 RPC 调用
        boolean isHttp = ObjectUtil.isNotNullEmpty(request);

        // 记录请求类型标识（WEB 或 RPC）
        logs.put(LogTag.TYPE, isHttp ? LogCommonType.WEB.getDesc() : LogCommonType.RPC.getDesc());

        // 记录来源地址：HTTP 请求取客户端 IP，RPC 调用取远程服务地址
        logs.put(LogTag.IP, isHttp ? WebUtil.getRequestHost(request) : rpcUrl);

        // 仅 HTTP 请求记录完整的请求 URL
        if(isHttp){
            logs.put(LogTag.URL, request.getRequestURL().toString());
        }

        // 记录被调用的方法全路径，格式为 "类全限定名:方法名"
        logs.put(LogTag.FUNCTION, proceedingJoinPoint.getSignature().getDeclaringTypeName()+":"+proceedingJoinPoint.getSignature().getName());

        return logs;

    }

}
