package com.mountain.user.api.impl;

import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.user.UserDo;
import com.mountain.user.rpc.api.UserServiceRpc;

public class UserServiceRpcImpl extends UserServiceImpl implements UserServiceRpc {
	
	
	//查询用户
	@Override
	public AbstractBaseResult<UserDo> rpcSelectUserOne(UserDo userDo) {
		return selectUserOne(UserDo.builder().username(userDo.getUsername()).build());
	}
}
