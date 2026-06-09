package com.maozi.log.convert;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.maozi.common.context.ApplicationEnvironmentContext;

/**
 * 应用名称日志转换器
 * <p>
 * 在日志输出中将 %appName 占位符替换为当前服务名称，
 * 用于在日志中标识产生日志的服务。
 * </p>
 *
 * @author maozi
 */
public class ApplicationNameMessageConverter extends MessageConverter {

    /**
     * 将日志占位符转换为服务名称
     *
     * @param event 日志事件
     * @return 当前服务名称
     */
    @Override
    public String convert(ILoggingEvent event) {
        return ApplicationEnvironmentContext.SERVICE_NAME;
    }
}
