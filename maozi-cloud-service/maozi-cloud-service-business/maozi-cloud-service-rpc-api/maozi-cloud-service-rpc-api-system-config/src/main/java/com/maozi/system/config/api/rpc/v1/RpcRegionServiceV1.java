package com.maozi.system.config.api.rpc.v1;

import com.maozi.base.result.DropDownResult;
import com.maozi.common.result.AbstractBaseResult;

public interface RpcRegionServiceV1 {
	
	AbstractBaseResult<DropDownResult> rpcGet(Long id);
	
}