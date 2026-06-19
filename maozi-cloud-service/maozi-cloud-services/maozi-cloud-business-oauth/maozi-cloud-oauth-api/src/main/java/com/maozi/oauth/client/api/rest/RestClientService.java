package com.maozi.oauth.client.api.rest;

import com.maozi.base.annotation.Get;
import com.maozi.base.annotation.Post;
import com.maozi.base.enums.Status;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.RequestParam;
import com.maozi.base.result.DropDownResult;
import com.maozi.base.result.PageResult;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.oauth.client.param.ClientListParam;
import com.maozi.oauth.client.param.ClientSaveUpdateParam;
import com.maozi.oauth.client.vo.ClientInfoVo;
import com.maozi.oauth.client.vo.ClientListVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 客户端管理 REST API 接口
 * <p>
 * 提供客户端（OAuth2 Client）的增删改查、状态更新及下拉列表等 REST 风格的 HTTP 接口定义。
 * 所有接口均基于 /client 路径，并通过 Spring Security 权限注解进行访问控制。
 * </p>
 */
@Tag(name = "客户端模块")
public interface RestClientService {

	/** 客户端模块的基础请求路径 */
	String PATH = "/client";

	/**
	 * 分页查询客户端列表
	 *
	 * @param pageParam 分页查询参数，包含客户端列表的筛选条件
	 * @return 返回客户端分页列表结果
	 */
	@Post(value = PATH + "/list",description = "列表")
	@PreAuthorize("hasAuthority('system:client:list')")
	AbstractBaseResult<PageResult<ClientListVo>> restList(@RequestBody PageParam<ClientListParam> pageParam);

	/**
	 * 保存（新增）客户端信息
	 *
	 * @param param 客户端保存/更新的请求参数
	 * @return 返回新增客户端的主键 ID
	 */
	@Post(value = PATH + "/save",description = "保存")
	@PreAuthorize("hasAuthority('system:client:save')")
	AbstractBaseResult<Long> restSave(@RequestBody ClientSaveUpdateParam param);

	/**
	 * 获取客户端下拉列表数据
	 * <p>
	 * 用于前端下拉选择框等场景，返回精简的客户端信息列表。
	 * 需要 system:user:list 权限。
	 * </p>
	 *
	 * @return 返回客户端下拉选项列表
	 */
	@Get(value = PATH + "/dropDownList",description = "下拉列表")
	@PreAuthorize("hasAuthority('system:user:list')")
	AbstractBaseResult<List<DropDownResult>> dropDownListResult();





//	==================== 单资源 ===================

	/** 单资源操作的基础路径，包含客户端 ID 路径变量 */
	String CURRENT_PATH = PATH + "/{id}";

	/**
	 * 根据客户端 ID 获取客户端详情信息
	 *
	 * @param id 客户端唯一标识 ID
	 * @return 返回客户端详细信息
	 */
	@Get(value = CURRENT_PATH + "/get",description = "详情")
	@PreAuthorize("hasAuthority('system:client:get')")
	AbstractBaseResult<ClientInfoVo> restGet(@PathVariable Long id);

	/**
	 * 根据客户端 ID 更新客户端信息
	 *
	 * @param id    客户端唯一标识 ID
	 * @param param 客户端保存/更新的请求参数
	 * @return 返回空结果，表示操作成功
	 */
	@Post(value = CURRENT_PATH + "/update",description = "更新")
	@PreAuthorize("hasAuthority('system:client:update')")
	AbstractBaseResult<Void> restUpdate(@PathVariable Long id,@RequestBody ClientSaveUpdateParam param);

	/**
	 * 根据客户端 ID 更新客户端状态（启用/禁用等）
	 *
	 * @param id    客户端唯一标识 ID
	 * @param param 状态更新参数，包含目标状态值
	 * @return 返回空结果，表示操作成功
	 */
	@Post(value = CURRENT_PATH + "/updateStatus",description = "更新状态")
	@PreAuthorize("hasAuthority('system:client:update')")
	AbstractBaseResult<Void> restUpdateStatus(@PathVariable Long id, @RequestBody RequestParam<Status> param);

	/**
	 * 根据客户端 ID 删除客户端
	 *
	 * @param id 客户端唯一标识 ID
	 * @return 返回空结果，表示操作成功
	 */
	@Post(value = CURRENT_PATH + "/delete",description = "删除")
	@PreAuthorize("hasAuthority('system:client:remove')")
	AbstractBaseResult<Void> restRemove(@PathVariable Long id);

}