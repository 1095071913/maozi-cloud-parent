package com.maozi.resource.config;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.maozi.sso.oauth.api.rest.v1.OauthTokenServiceRestV1;
import com.maozi.sso.oauth.api.rpc.v1.OauthTokenServiceRpcV1;

@Configuration
public class OauthTokenServiceConfig {
	
	@DubboReference
	public OauthTokenServiceRpcV1 oauthTokenServiceRpc;
	
	@Autowired
	public OauthTokenServiceRestV1 oauthTokenServiceRest;
	
}
