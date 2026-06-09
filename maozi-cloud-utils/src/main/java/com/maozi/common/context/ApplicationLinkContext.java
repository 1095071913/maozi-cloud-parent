package com.maozi.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlWrappers;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * 应用链路上下文
 * <p>
 * 使用 TransmittableThreadLocal 存储当前请求的版本号和用户名等链路信息，
 * 支持线程池和异步场景下的上下文传递。同时提供 Consumer 包装和上下文清理功能。
 * </p>
 *
 * @author maozi
 */
@Data
public class ApplicationLinkContext {

    /** 版本号请求头名称 */
    public static final String VERSION = "X-Version";

    /** 用户名请求头名称 */
    public static final String USERNAME = "X-Username";

    /** Nacos 元数据中的版本键 */
    public static final String NACOS_VERSION = "version";

    /** 默认应用版本（主版本） */
    public static final String APPLICATION_DEFAULT_VERSION = "main";

    /** 当前线程的版本号 */
    public static TransmittableThreadLocal<String> VERSIONS = new TransmittableThreadLocal<>();

    /** 当前线程的用户名 */
    public static TransmittableThreadLocal<String> USERNAMES = new TransmittableThreadLocal<>();

    /**
     * 获取版本号，为空时返回默认版本
     *
     * @param version 版本号对象
     * @return 版本号字符串
     */
    public static String getVersionDefault(Object version){
        return ObjectUtil.isNotNullEmpty(version) && StringUtils.isNotBlank(version.toString()) ? version.toString() : ApplicationLinkContext.APPLICATION_DEFAULT_VERSION;
    }

    /**
     * 包装 Consumer，确保执行后自动清理上下文并支持 TTL 传递
     *
     * @param consumer 原始 Consumer
     * @param <T> Consumer 参数类型
     * @return 包装后的 Consumer
     */
    public static <T> Consumer<T> wrapConsumer(Consumer<T> consumer) {

        if (ObjectUtil.isNullEmpty(consumer)) {
            return null;
        }

        consumer = consumer.andThen((value)-> clearContext());

        return TtlWrappers.wrapConsumer(consumer);

    }

    /**
     * 清理当前线程的链路上下文
     */
    public static void clearContext(){
        LogUtil.sqlLog.remove();
        VERSIONS.remove();
        USERNAMES.remove();
    }

}
