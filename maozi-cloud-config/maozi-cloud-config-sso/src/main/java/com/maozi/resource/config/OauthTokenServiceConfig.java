package com.maozi.resource.config;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.maozi.sso.oauth.api.rest.v1.RestOauthTokenServiceV1;
import com.maozi.sso.oauth.api.rpc.v1.RpcOauthTokenServiceV1;

@Configuration
public class OauthTokenServiceConfig {
	
	@DubboReference
	public RpcOauthTokenServiceV1 rpcOauthTokenService;
	
	@Resource
	public RestOauthTokenServiceV1 restOauthTokenService;
	
}
