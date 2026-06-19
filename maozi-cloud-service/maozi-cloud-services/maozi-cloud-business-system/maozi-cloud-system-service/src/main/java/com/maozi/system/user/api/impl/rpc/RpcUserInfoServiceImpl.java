package com.maozi.system.user.api.impl.rpc;

import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.system.user.api.impl.UserServiceImpl;
import com.maozi.system.user.api.rpc.RpcUserInfoService;
import com.maozi.system.user.dto.SystemUser;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 用户信息RPC服务实现类
 * <p>
 * 提供用户信息的Dubbo RPC远程调用接口实现，用于跨服务查询用户基本信息。
 * 继承自 UserServiceImpl，复用用户基础业务逻辑。
 * </p>
 */
@DubboService
public class RpcUserInfoServiceImpl extends UserServiceImpl implements RpcUserInfoService {

	/**
	 * 根据用户名查询系统用户信息
	 * <p>
	 * 通过用户名查询用户信息，支持自定义查询字段，结果映射为 SystemUser 对象。
	 * 供其他微服务远程调用获取用户基本信息。
	 * </p>
	 *
	 * @param username 用户名
	 * @param columns  需要查询的字段列表，可变参数
	 * @return 系统用户信息对象
	 */
	@Override
	public AbstractBaseResult<SystemUser> rpcGetByUsername(String username, String... columns) {
		return ResultUtil.success(getByUsername(username,SystemUser.class,columns));
	}

}
