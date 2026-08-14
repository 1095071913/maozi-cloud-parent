package com.maozi.system.user.api.impl.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.service.api.annotation.RemoteService;
import com.maozi.system.user.api.impl.UserServiceImpl;
import com.maozi.system.user.api.rpc.RpcUserService;
import com.maozi.system.user.domain.UserDo;
import com.maozi.system.user.result.OauthUserInfoResult;

import java.util.List;

/**
 * 用户RPC服务实现类
 * <p>
 * 提供用户模块的Dubbo RPC远程调用接口实现，包括根据用户名查询密码、
 * 根据用户名获取全部权限标识、根据用户名和指定角色获取权限标识等功能。
 * 继承自 UserServiceImpl，复用用户基础业务逻辑。
 * </p>
 */
@RemoteService
public class RpcUserServiceImpl extends UserServiceImpl implements RpcUserService {

	@Override
	public AbstractBaseResult<OauthUserInfoResult> rpcGetOauthUserInfoByUsername(String username) {

		ObjectUtil.isNullEmptyThrowError(username, getResourceName());

		LambdaQueryWrapper<UserDo> wrapper = Wrappers.lambdaQuery();

		wrapper.select(UserDo::getId,UserDo::getPassword);
		wrapper.eq(UserDo::getUsername,username);

		UserDo domain = getOne(wrapper);

		ObjectUtil.isNullEmptyThrowError(domain, getResourceName());

		Long userId = domain.getId();
		return ResultUtil.success(new OauthUserInfoResult(userId,domain.getPassword(),getPermissionsByUserId(userId)));

	}

	/**
	 * 根据用户名和指定角色ID获取该角色下的权限标识列表
	 * <p>
	 * 先验证用户是否绑定了指定角色，如果未绑定则抛出异常。
	 * 然后查询指定角色关联的权限标识列表。
	 * </p>
	 *
	 * @param username 用户名
	 * @param roleId   角色ID
	 * @return 指定角色下的权限标识列表
	 */
	@Override
	public AbstractBaseResult<List<String>> rpcGetPermissionsByUsernameRole(String username, Long roleId) {

		Long id = getAvailableByUsername(username);

		userRoleService.hasUserBindRole(id,roleId);

		List<String> responses = CollectionUtil.newArrayList();

		List<Long> permissionIds = rolePermissionService.getPermissionsByRole(roleId);

		List<String> marks = permissionService.getMarks(permissionIds);

        responses.addAll(marks);

		return ResultUtil.success(responses);

	}

}