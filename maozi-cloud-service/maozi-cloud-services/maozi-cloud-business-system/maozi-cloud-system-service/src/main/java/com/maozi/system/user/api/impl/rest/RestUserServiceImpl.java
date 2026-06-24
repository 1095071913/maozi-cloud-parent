/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.maozi.system.user.api.impl.rest;

import com.maozi.base.api.annotation.RestService;
import com.maozi.base.enums.Status;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.RequestParam;
import com.maozi.base.result.PageResult;
import com.maozi.common.ResultUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.dto.CurrentUserInfo;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.system.user.api.impl.UserServiceImpl;
import com.maozi.system.user.api.rest.RestUserService;
import com.maozi.system.user.domain.UserDo;
import com.maozi.system.user.dto.UserListParam;
import com.maozi.system.user.dto.UserSaveUpdateParam;
import com.maozi.system.user.vo.UserIndividualInfoVo;
import com.maozi.system.user.vo.UserInfoVo;
import com.maozi.system.user.vo.UserListVo;

/**
 * 用户REST服务实现类
 * <p>
 * 提供用户模块的RESTful API接口实现，包括用户的分页查询、新增、删除、
 * 详情查询、更新、状态变更以及当前登录用户个人信息查询等功能。
 * 继承自 UserServiceImpl，复用用户基础业务逻辑。
 * </p>
 */
@RestService
public class RestUserServiceImpl extends UserServiceImpl implements RestUserService {

	/**
	 * 分页查询用户列表
	 *
	 * @param pageParam 分页查询参数，包含分页信息和用户列表查询条件
	 * @return 分页查询结果，包含用户列表数据
	 */
	@Override
	public AbstractBaseResult<PageResult<UserListVo>> restList(PageParam<UserListParam> pageParam) {
		return ResultUtil.success(listRelation(pageParam, UserListVo.class));
	}

	/**
	 * 新增用户
	 *
	 * @param param 用户新增参数，包含用户基本信息和角色绑定信息
	 * @return 新增用户的ID
	 */
	@Override
	public AbstractBaseResult<Long> restSave(UserSaveUpdateParam param) {
		return ResultUtil.success(restSaveUpdate(null,param));
	}

	/**
	 * 删除用户
	 *
	 * @param id 需要删除的用户ID
	 * @return 操作结果
	 */
	@Override
	public AbstractBaseResult<Void> restRemove(Long id) {

		// 先获取用户的用户名和客户端ID，删除后需用于注销认证令牌
		UserDo user = getById(id, UserDo::getUsername,UserDo::getClientId);

		// 删除用户数据库记录
		AbstractBaseResult<Void> responseResult = removeByIdResult(id);

		// 通过RPC调用OAuth服务，根据clientId+用户名销毁该用户的所有OAuth2授权令牌
		rpcOauthTokenService.rpcDestroyByPrincipal(user.getClientId().toString(), user.getUsername()).getResultDataThrowError();

		return responseResult;

	}

	/**
	 * 查询用户详情
	 *
	 * @param id 用户ID
	 * @return 用户详细信息
	 */
	@Override
	public AbstractBaseResult<UserInfoVo> restGet(Long id) {
		return ResultUtil.success(getByIdThrowErrorRelation(id, UserInfoVo.class));
	}

	/**
	 * 更新用户信息
	 *
	 * @param id    需要更新的用户ID
	 * @param param 用户更新参数，包含需要更新的用户信息
	 * @return 操作结果
	 */
	@Override
	public AbstractBaseResult<Void> restUpdate(Long id, UserSaveUpdateParam param) {

		restSaveUpdate(id,param);

		if(Status.DISABLE == param.getStatus()){
			UserDo user = getByIdThrowError(id,UserDo::getUsername,UserDo::getClientId);
			rpcOauthTokenService.rpcDestroyByPrincipal(user.getClientId().toString(), user.getUsername()).getResultDataThrowError();
		}

		return ResultUtil.success();

	}

	/**
	 * 更新用户状态（启用/禁用）
	 * <p>
	 * 如果用户当前状态与目标状态一致，则不做任何操作直接返回成功。
	 * </p>
	 *
	 * @param id    需要更新状态的用户ID
	 * @param param 状态参数，包含目标状态值
	 * @return 操作结果
	 */
	@Override
	public AbstractBaseResult<Void> restUpdateStatus(Long id, RequestParam<Status> param){

		Status status = param.getData();

		UserDo user = getByIdThrowError(id,UserDo::getUsername,UserDo::getClientId,UserDo::getStatus);
		if(user.getStatus() == status){
			return ResultUtil.success();
		}

		user.setId(id);
		user.setStatus(status);
		updateById(user);

		if(Status.DISABLE == status){
			rpcOauthTokenService.rpcDestroyByPrincipal(String.valueOf(user.getClientId()), user.getUsername()).getResultDataThrowError();
		}

		return ResultUtil.success();

	}

	/**
	 * 获取当前登录用户的个人信息
	 * <p>
	 * 从应用上下文中获取当前登录用户的用户名，查询并返回该用户的姓名和头像信息。
	 * </p>
	 *
	 * @return 当前登录用户的个人信息
	 */
	@Override
	public AbstractBaseResult<UserIndividualInfoVo> restIndividualGet() {
		return ResultUtil.success(getByUsername(ApplicationLinkContext.getCurrentUserInfo(CurrentUserInfo::getUsername), UserIndividualInfoVo.class,getColumns(UserDo::getName,UserDo::getIcon)));
	}

}