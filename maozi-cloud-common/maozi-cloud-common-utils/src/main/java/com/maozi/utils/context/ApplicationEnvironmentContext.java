package com.maozi.utils.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Data
@Component
@RefreshScope(proxyMode = ScopedProxyMode.NO)
public class ApplicationEnvironmentContext {

	public static boolean IS_RUNNING = false;

	public static String applicationProjectAbbreviation;
	@Value("${application-project-abbreviation}")
	public void setApplicationProjectAbbreviation(String applicationProjectAbbreviation) {
		ApplicationEnvironmentContext.applicationProjectAbbreviation = applicationProjectAbbreviation;
	}

	public static String SERVICE_NAME;
	@Value("${spring.application.name}")
	public void setServiceName(String serviceName) {
		ApplicationEnvironmentContext.SERVICE_NAME = serviceName;
	}

	public static String SERVICE_PORT;
	@Value("${server.port}")
	public void setServicePort(String servicePort) {
		ApplicationEnvironmentContext.SERVICE_PORT = servicePort;
	}
	
	public static String LOAD_CONFIG;
	@Value("${spring.cloud.nacos.config.shared-dataids}")
	public void setLoadConfig(String loadConfig) {
		ApplicationEnvironmentContext.LOAD_CONFIG = loadConfig;
	}
	
	public static String CONFIG_ADDR;
	@Value("${spring.cloud.nacos.config.server-addr}")
	public void setConfigAddr(String configAddr) {
		ApplicationEnvironmentContext.CONFIG_ADDR = configAddr;
	}
	
	public static String ENVIRONMENT;
	@Value("${project.environment}")
	public void setEnvironment(String environment) {
		ApplicationEnvironmentContext.ENVIRONMENT = environment;
	}

	public static String TITLE;
	@Value("${project.title}")
	public void setTitle(String title) {
		ApplicationEnvironmentContext.TITLE = title;
	}

	public static String VERSION;
	@Value("${project.version:main}")
	public void setVersion(String version) {
		ApplicationEnvironmentContext.VERSION = version;
	}

	public static String DETAILS;
	@Value("${project.details}")
	public void setDetails(String details) {
		ApplicationEnvironmentContext.DETAILS = details;
	}
	
	public static Environment CONFIG;
	@Resource
	public void setEnvironmentConfig(Environment environmentConfig) {
		ApplicationEnvironmentContext.CONFIG = environmentConfig;
	}

}
