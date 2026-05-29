package com.maozi.system.user.api.rpc;

import com.maozi.common.result.AbstractBaseResult;
import com.maozi.system.user.dto.global.dto.SystemUser;

public interface RpcUserInfoService {

	AbstractBaseResult<SystemUser> rpcGetByUsername(String username,String ... columns);
	
}