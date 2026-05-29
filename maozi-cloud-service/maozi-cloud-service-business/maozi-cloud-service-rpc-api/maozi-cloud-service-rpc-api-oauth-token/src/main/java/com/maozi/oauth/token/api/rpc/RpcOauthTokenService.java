package com.maozi.oauth.token.api.rpc;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.dto.platform.param.ClientUserParam;

import java.util.List;
import java.util.Map;

public interface RpcOauthTokenService {
 
	AbstractBaseResult<Map<String,?>> rpcCheck(String token);
	
	AbstractBaseResult<Void> rpcDestroy(String token);
	
	AbstractBaseResult<Void> rpcDestroy(ClientUserParam param);
	
	AbstractBaseResult<Void> rpcDestroys(List<ClientUserParam> params);
	
}
