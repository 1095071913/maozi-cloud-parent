package com.maozi.log.convert;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.maozi.common.context.ApplicationEnvironmentContext;

/**
 * 环境信息日志转换器
 * <p>
 * 在日志输出中将 %env 占位符替换为当前运行环境标识（如 local、test、prod），
 * 用于在日志中区分不同环境。
 * </p>
 *
 * @author maozi
 */
public class EnvironmentMessageConverter extends MessageConverter {

    /**
     * 将日志占位符转换为环境标识
     *
     * @param event 日志事件
     * @return 当前环境标识
     */
    @Override
    public String convert(ILoggingEvent event) {
        return ApplicationEnvironmentContext.ENVIRONMENT;
    }
}
