package com.maozi.log.convert;

import com.maozi.tool.ApplicationEnvironmentConfig;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
 
public class ApplicationNameMessageConverter extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return ApplicationEnvironmentConfig.applicationName; 
    }
}