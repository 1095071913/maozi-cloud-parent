package com.maozi.sentry.config;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ch.qos.logback.classic.LoggerContext;
import io.sentry.SentryOptions;
import io.sentry.logback.SentryAppender;

@Configuration
@EnableConfigurationProperties(value = { SentryProperties.class })
public class SentryAutoConfiguration implements CommandLineRunner {
	@Autowired
	private final SentryProperties sentryProperties;

	private boolean enabled = false;
	
	@Autowired
	public SentryAutoConfiguration(SentryProperties properties) {
		this.sentryProperties = properties;
		this.enabled = Objects.nonNull(properties) && properties.isEnabled() && StringUtils.isNotBlank(properties.getDsn());
		/*
		if (enabled) {
			initSentryAppenderInLogback();
		} else {
			log.warn("读取不了Sentry配置，或配置有遗漏，请检查nacos配置");
		}
		*/
	}

//	@PostConstruct
	private void initSentryAppenderInLogback() {
		if (!enabled) {
			return;
		}
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

		SentryOptions so = new SentryOptions();
		so.setDsn(sentryProperties.getDsn());
		so.setMaxBreadcrumbs(sentryProperties.getMaxBreadcrumbs());
		if(StringUtils.isNotEmpty(sentryProperties.getEnvironment())) {
			so.setEnvironment(sentryProperties.getEnvironment());
		}
		
		SentryAppender sa = new SentryAppender();
		sa.setOptions(so);
		sa.start();
		
		ch.qos.logback.classic.Logger targetLogger = loggerContext.getLogger(sentryProperties.getLoggerName());
		if(null != targetLogger) {
			targetLogger.addAppender(sa);
		}
		
		
	}

	@Override
	public void run(String... args) throws Exception {
		initSentryAppenderInLogback();
	}

}
