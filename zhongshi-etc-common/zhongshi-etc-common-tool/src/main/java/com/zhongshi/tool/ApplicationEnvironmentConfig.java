package com.zhongshi.tool;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@RefreshScope(proxyMode = ScopedProxyMode.NO)
public class ApplicationEnvironmentConfig {

	public static String applicationName;
	@Value("${spring.application.name}")
	public void setApplicationName(String applicationName) {
		this.applicationName=applicationName;
	}
	
	public static String loadConfig;
	@Value("${spring.cloud.nacos.config.shared-dataids}")
	public void setLoadConfig(String loadConfig) {
		this.loadConfig = loadConfig;
	}
	
	public static String nacosAddr;
	@Value("${spring.cloud.nacos.config.server-addr}")
	public void setNacosAddr(String nacosAddr) {
		this.nacosAddr = nacosAddr;
	}

	public static String rpcServerNames;
	@Value("${application-rpc-service-name:#{null}}")
	public void setRpcServerNames(String rpcServerNames) {
		this.rpcServerNames = rpcServerNames;
	}
	
	public static String environment;
	@Value("${project.environment}")
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public static Boolean isTest;
	@Value("${logging.test}")
	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}	
	
	public static String dingdingToken;
	@Value("${project.dingdingToken:f4152fa1590798ccbc7b927a30473ea34274dc904fb31b771f5d63b4f20e56f1}")
	public void setDingdingToken(String dingdingToken) {
		this.dingdingToken = dingdingToken;
	}
	
	private static Environment environmentConfig;
	@Resource
	public void setEnvironmentConfig(Environment environmentConfig) {
		this.environmentConfig=environmentConfig;
	}

	
	
}
