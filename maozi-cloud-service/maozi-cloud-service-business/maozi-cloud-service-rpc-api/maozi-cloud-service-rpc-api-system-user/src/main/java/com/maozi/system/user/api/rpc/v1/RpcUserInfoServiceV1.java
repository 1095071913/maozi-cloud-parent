package com.maozi.system.user.api.rpc.v1;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.user.SystemUser;

public interface RpcUserInfoServiceV1 {

	AbstractBaseResult<SystemUser> rpcGetByUsername(String username,String ... colums);
	
}