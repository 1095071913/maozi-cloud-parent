package com.maozi.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.maozi.common.JacksonUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.dto.CurrentUserInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.function.Function;

/**
 * 应用链路上下文
 * <p>
 * 使用 TransmittableThreadLocal 存储当前请求的链路信息，包括版本号、当前登录用户信息（{@link CurrentUserInfo}）、
 * 以及链路追踪 ID（traceId，并同步写入 MDC 便于日志关联），支持线程池和异步场景下的上下文传递。
 * 同时提供 Consumer 包装和上下文清理功能。
 * </p>
 *
 * @author maozi
 */
@Data
public class ApplicationLinkContext {

    /** 版本号请求头名称 */
    public static final String VERSION_KEY = "X-Version";

    /** 当前登录用户信息请求头名称 */
    public static final String CURRENT_USER_INFO_KEY = "X-CurrentUserInfo";

    /** 请求链路唯一ID */
    public static final String TRACE_ID_KEY = "X-TraceId";

    public static final String TRACE_ID_VALUE = "00000000000000000000000000000000";

    /** Nacos 元数据中的版本键 */
    public static final String NACOS_VERSION_KEY = "version";

    /** 默认应用版本（主版本） */
    public static final String APPLICATION_DEFAULT_VERSION = "main";

    public static final String MDC_TRACE_ID_KEY = "trace_id";

    /** 当前线程的版本号（用于灰度路由） */
    public static TransmittableThreadLocal<String> versions = new TransmittableThreadLocal<>();

    /** 当前线程的登录用户信息（含用户名、客户端 ID） */
    public static TransmittableThreadLocal<CurrentUserInfo> currentUserInfos = new TransmittableThreadLocal<>();

    /** 当前线程的链路请求ID */
    public static TransmittableThreadLocal<String> traceIds = new TransmittableThreadLocal<>();

    /**
     * 通过函数式接口提取当前登录用户的指定属性
     * <p>
     * 当上下文中未携带用户信息时返回 {@code null}，避免 NPE。
     * </p>
     *
     * @param function 从 {@link CurrentUserInfo} 提取目标属性的函数（如 {@code CurrentUserInfo::getUsername}）
     * @param <R> 目标属性类型
     * @return 当前用户的目标属性值；未登录或上下文缺失时返回 {@code null}
     */
    public static <R> R getCurrentUserInfo(Function<CurrentUserInfo, R> function) {
        CurrentUserInfo currentUserInfo = currentUserInfos.get();
        if(ObjectUtil.isNullEmpty(currentUserInfo)){
            return null;
        }
        return function.apply(currentUserInfo);
    }

    /**
     * 获取版本号，为空时返回默认版本
     *
     * @param version 版本号对象
     * @return 版本号字符串
     */
    public static String getVersionDefault(Object version){
        return ObjectUtil.isNotNullEmpty(version) && StringUtils.isNotBlank(version.toString()) ? version.toString() : ApplicationLinkContext.APPLICATION_DEFAULT_VERSION;
    }

    public static void setCurrentUserInfo(String currentUserInfoJson){
        if(ObjectUtil.isNotNullEmpty(currentUserInfoJson)){
            ApplicationLinkContext.currentUserInfos.set(JacksonUtil.jsonToObject(currentUserInfoJson, CurrentUserInfo.class));
        }
    }

    public static void setTraceId(String traceId){
        if(ObjectUtil.isNotNullEmpty(traceId)){
            traceIds.set(traceId);
            MDC.put(MDC_TRACE_ID_KEY,traceId);
        }
    }

    /**
     * 清理当前线程的链路上下文
     */
    public static void clearContext(){
        LogUtil.sqlLog.remove();
        versions.remove();
        currentUserInfos.remove();
        traceIds.remove();
        MDC.clear();
    }

}
