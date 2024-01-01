package com.maozi.log.convert;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.maozi.utils.context.ApplicationEnvironmentContext;
 
public class EnvironmentMessageConverter extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return ApplicationEnvironmentContext.environment;
    }
}