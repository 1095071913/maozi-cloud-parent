package com.zhongshi.resource.config;

import org.apache.dubbo.config.annotation.DubboReference;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.sso.api.OauthTokenServiceRpc;

public class OauthTokenServiceConfig extends BaseResultFactory {
	
	@DubboReference
	public OauthTokenServiceRpc oauthTokenServiceRpc;
	
}
