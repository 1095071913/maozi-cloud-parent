package com.maozi.system.config.api.rpc;

import com.maozi.base.result.DropDownResult;
import com.maozi.common.result.AbstractBaseResult;

public interface RpcRegionService {
	
	AbstractBaseResult<DropDownResult> rpcGet(Long id);
	
}