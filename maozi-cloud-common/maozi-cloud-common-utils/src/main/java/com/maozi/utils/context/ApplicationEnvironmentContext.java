package com.maozi.utils.context;

import javax.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope(proxyMode = ScopedProxyMode.NO)
public class ApplicationEnvironmentContext {

	public static String applicationProjectAbbreviation;
	@Value("${application-project-abbreviation}")
	public void setApplicationProjectAbbreviation(String applicationProjectAbbreviation) {
		ApplicationEnvironmentContext.applicationProjectAbbreviation=applicationProjectAbbreviation;
	}

	public static String applicationName;
	@Value("${spring.application.name}")
	public void setApplicationName(String applicationName) {
		ApplicationEnvironmentContext.applicationName=applicationName;
	}
	
	public static String loadConfig;
	@Value("${spring.cloud.nacos.config.shared-dataids}")
	public void setLoadConfig(String loadConfig) {
		ApplicationEnvironmentContext.loadConfig = loadConfig;
	}
	
	public static String nacosAddr;
	@Value("${spring.cloud.nacos.config.server-addr}")
	public void setNacosAddr(String nacosAddr) {
		ApplicationEnvironmentContext.nacosAddr = nacosAddr;
	}
	
	public static String environment;
	@Value("${project.environment}")
	public void setEnvironment(String environment) {
		ApplicationEnvironmentContext.environment = environment;
	}

	public static String title;
	@Value("${project.title}")
	public void setTitle(String title) {
		ApplicationEnvironmentContext.title = title;
	}

	public static String version;
	@Value("${project.version:null}")
	public void setVersion(String version) {
		ApplicationEnvironmentContext.version = version;
	}

	public static String details;
	@Value("${project.details}")
	public void setDetails(String details) {
		ApplicationEnvironmentContext.details = details;
	}
	
	public static String dingdingToken;
	@Value("${project.dingdingToken:f4152fa1590798ccbc7b927a30473ea34274dc904fb31b771f5d63b4f20e56f1}")
	public void setDingdingToken(String dingdingToken) {
		ApplicationEnvironmentContext.dingdingToken = dingdingToken;
	}
	
	public static Environment environmentConfig;
	@Resource
	public void setEnvironmentConfig(Environment environmentConfig) {
		ApplicationEnvironmentContext.environmentConfig=environmentConfig;
	}

	
}
