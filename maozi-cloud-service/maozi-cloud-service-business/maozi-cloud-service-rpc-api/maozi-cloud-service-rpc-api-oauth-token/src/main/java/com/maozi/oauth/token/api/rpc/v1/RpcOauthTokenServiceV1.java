package com.maozi.oauth.token.api.rpc.v1;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.token.dto.platform.dto.OauthToken;
import com.maozi.oauth.token.dto.platform.param.ClientParam;
import com.maozi.oauth.token.dto.platform.param.ClientUserParam;
import com.maozi.oauth.token.dto.platform.param.TokenInfoParam;
import java.util.List;
import java.util.Map;

public interface RpcOauthTokenServiceV1 {
	
	AbstractBaseResult<OauthToken> rpcGet(TokenInfoParam param) throws Exception;
	
	AbstractBaseResult<OauthToken> rpcRefresh(String token,ClientParam param) throws Exception;
 
	AbstractBaseResult<Map> rpcCheck(String token);
	
	AbstractBaseResult<Void> rpcDestroy(String token);
	
	AbstractBaseResult<Void> rpcDestroy(ClientUserParam param);
	
	AbstractBaseResult<Void> rpcDestroys(List<ClientUserParam> params);
	
}
