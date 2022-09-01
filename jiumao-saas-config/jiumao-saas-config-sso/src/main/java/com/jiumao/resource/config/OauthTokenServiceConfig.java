package com.jiumao.resource.config;

import org.apache.dubbo.config.annotation.DubboReference;

import com.jiumao.factory.BaseResultFactory;
import com.jiumao.sso.api.OauthTokenServiceRpcV1;

public class OauthTokenServiceConfig extends BaseResultFactory {
	
	@DubboReference
	public OauthTokenServiceRpcV1 oauthTokenServiceRpc;
	
}
