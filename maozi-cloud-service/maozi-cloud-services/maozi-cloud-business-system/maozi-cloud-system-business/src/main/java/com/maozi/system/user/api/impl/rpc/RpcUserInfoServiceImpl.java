package com.maozi.system.user.api.impl.rpc;

import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.service.api.annotation.RemoteService;
import com.maozi.system.user.api.impl.UserServiceImpl;
import com.maozi.system.user.api.rpc.RpcUserInfoService;
import com.maozi.system.user.dto.SystemUser;

/**
 * 用户信息RPC服务实现类
 * <p>
 * 提供用户信息的Dubbo RPC远程调用接口实现，用于跨服务查询用户基本信息。
 * 继承自 UserServiceImpl，复用用户基础业务逻辑。
 * </p>
 */
@RemoteService
public class RpcUserInfoServiceImpl extends UserServiceImpl implements RpcUserInfoService {

	@Override
	public AbstractBaseResult<SystemUser> rpcGetById(Long id, String... columns) {
		return ResultUtil.success(getByIdThrowErrorRelation(id,SystemUser.class));
	}

}
