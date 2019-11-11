package com.mountain.user.api.impl;

import java.util.concurrent.ExecutionException;

import com.mountain.factory.result.AbstractBaseResult;
import com.mountain.user.UserDo;
import com.mountain.user.rpc.api.UserServiceRpc;

public class UserServiceRpcImpl extends UserServiceImpl implements UserServiceRpc {
	
	
	//查询用户
	@Override
	public AbstractBaseResult<UserDo> rpcSelectUserOne(UserDo userDo){
		UserDo user = null;
		try {
			user = selectOne(userDo).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return isNotNull(user) ? error(code(40010)) : success(user) ;
	}
}
