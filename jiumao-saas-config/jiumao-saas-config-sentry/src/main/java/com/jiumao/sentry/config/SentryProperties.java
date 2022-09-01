package com.jiumao.sentry.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("sentry")
public class SentryProperties {
	private boolean enabled = false;
	private String dsn;
	private int maxBreadcrumbs = 50;
	private String environment = "develop";
	private String loggerName = "com.zhongshi";
}
