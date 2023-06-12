package com.maozi.sso.oauth.api.rpc.v1;

import java.util.List;
import java.util.Map;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.sso.oauth.dto.platform.dto.OauthToken;
import com.maozi.sso.oauth.dto.platform.param.ClientParam;
import com.maozi.sso.oauth.dto.platform.param.ClientUserParam;
import com.maozi.sso.oauth.dto.platform.param.TokenInfoParam;

public interface RpcOauthTokenServiceV1 {
	
	AbstractBaseResult<OauthToken> rpcGet(TokenInfoParam param) throws Exception;
	
	AbstractBaseResult<OauthToken> rpcRefresh(String token,ClientParam param) throws Exception;
 
	AbstractBaseResult<Map> rpcCheck(String token);
	
	AbstractBaseResult<Void> rpcDestroy(String token);
	
	AbstractBaseResult<Void> rpcDestroy(ClientUserParam param);
	
	AbstractBaseResult<Void> rpcDestroys(List<ClientUserParam> params);
	
}
