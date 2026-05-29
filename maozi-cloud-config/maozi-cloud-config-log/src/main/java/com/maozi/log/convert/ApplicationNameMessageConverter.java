package com.maozi.log.convert;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.maozi.common.context.ApplicationEnvironmentContext;
 
public class ApplicationNameMessageConverter extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return ApplicationEnvironmentContext.SERVICE_NAME;
    }
}