package com.maozi.system.permission.api.impl.rest;

import com.maozi.base.annotation.RestService;
import com.maozi.base.enums.Status;
import com.maozi.base.param.RequestParam;
import com.maozi.base.result.DropDownResult;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.system.permission.api.impl.RoleServiceImpl;
import com.maozi.system.permission.api.rest.RestRoleService;
import com.maozi.system.permission.domain.RoleDo;
import com.maozi.system.role.dto.RoleSaveUpdateParam;
import com.maozi.system.role.vo.RoleInfoVo;
import com.maozi.system.role.vo.RoleListVo;

import java.util.List;

/**
 * 角色REST服务实现类
 * <p>提供角色管理的RESTful接口实现，包括角色列表查询、
 * 角色新增、角色详情查询、角色更新、角色删除、角色状态下拉列表等功能。</p>
 */
@RestService
public class RestRoleServiceImpl extends RoleServiceImpl implements RestRoleService {

	/**
	 * 查询角色列表
	 *
	 * @return 角色列表视图数据
	 */
	@Override
	public AbstractBaseResult<List<RoleListVo>> restList() {
		return ResultUtil.success(list(RoleListVo.class, RoleDo::getId,RoleDo::getName));
	}

	/**
	 * 新增角色
	 *
	 * @param param 角色保存参数
	 * @return 新增角色的ID
	 */
	@Override
	public AbstractBaseResult<Long> restSave(RoleSaveUpdateParam param) {
		return ResultUtil.success(restSaveUpdate(null,param));
	}

	/**
	 * 查询角色下拉列表
	 *
	 * @return 角色下拉选项列表
	 */
	@Override
	public AbstractBaseResult<List<DropDownResult>> dropDownListResult(){
		return super.dropDownListResult();
	}

	/**
	 * 查询角色详情
	 *
	 * @param id 角色ID
	 * @return 角色详情视图数据
	 */
	@Override
	public AbstractBaseResult<RoleInfoVo> restGet(Long id) {
		return ResultUtil.success(getByIdThrowErrorRelation(id, RoleInfoVo.class));
	}

	/**
	 * 删除角色
	 *
	 * @param id 角色ID
	 * @return 操作结果
	 */
	@Override
	public AbstractBaseResult<Void> restRemove(Long id) {
		return removeByIdResult(id);
	}

	/**
	 * 更新角色
	 *
	 * @param id 角色ID
	 * @param param 角色更新参数
	 * @return 操作结果
	 */
	@Override
	public AbstractBaseResult<Void> restUpdate(Long id, RoleSaveUpdateParam param) {

		restSaveUpdate(id, param);

		return ResultUtil.success();

	}

	/**
	 * 更新角色状态
	 *
	 * @param id 角色ID
	 * @param param 状态参数（包含启用/禁用状态）
	 * @return 操作结果
	 */
	@Override
	public AbstractBaseResult<Void> updateStatus(Long id, RequestParam<Status> param){

		// 禁用前校验是否有用户绑定该角色，有则抛出异常阻止禁用
		if(param.getData() == Status.DISABLE){
			checkBind(id);
		}

		return super.updateStatus(id, param.getData());

	}

}
