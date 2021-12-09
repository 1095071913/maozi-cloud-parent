package com.zhongshi.log.convert;

import com.zhongshi.tool.ApplicationEnvironmentConfig;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
 
public class EnvironmentMessageConverter extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return ApplicationEnvironmentConfig.environment; 
    }
}