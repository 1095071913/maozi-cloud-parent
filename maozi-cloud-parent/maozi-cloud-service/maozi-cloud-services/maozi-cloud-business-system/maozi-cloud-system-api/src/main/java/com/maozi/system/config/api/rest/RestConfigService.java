package com.maozi.system.config.api.rest;

import com.maozi.base.enums.Status;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.RequestParam;
import com.maozi.base.result.PageResult;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.service.annotation.Get;
import com.maozi.service.annotation.Post;
import com.maozi.system.config.dto.ConfigListParam;
import com.maozi.system.config.dto.ConfigSaveUpdateParam;
import com.maozi.system.config.vo.ConfigDropDownResult;
import com.maozi.system.config.vo.ConfigInfoVo;
import com.maozi.system.config.vo.ConfigListVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 全局配置 REST 接口
 * <p>
 * 提供系统全局配置的 RESTful API 接口定义，
 * 包括配置分页列表查询、配置新增、配置详情查询、
 * 配置删除、配置更新、配置状态更新、配置下拉列表
 * 以及根据配置名称获取配置值等操作。
 * </p>
 */
@Tag(name = "配置模块")
public interface RestConfigService {

	/** 基础路径常量，全局配置模块的统一请求路径前缀 */
	String PATH = "/config";

	/**
	 * 获取全局配置分页列表
	 *
	 * @param pageParam 分页查询参数，包含页码、每页数量以及配置搜索条件（类型、名称、别名）
	 * @return 返回配置分页列表数据，包含配置 ID、名称、别名、类型、配置值、状态、创建时间等信息
	 */
	@Post(value = PATH + "/list",description = "配置列表")
	@PreAuthorize("hasAuthority('system:config:list')")
	AbstractBaseResult<PageResult<ConfigListVo>> restList(@RequestBody @Valid PageParam<ConfigListParam> pageParam);

	/**
	 * 保存新增全局配置
	 *
	 * @param param 配置保存参数，包含配置名称、类型、配置值
	 * @return 返回新增配置的 ID
	 */
	@Post(value = PATH + "/save",description = "配置保存")
	@PreAuthorize("hasAuthority('system:config:save')")
	AbstractBaseResult<Long> restSave(@RequestBody @Valid ConfigSaveUpdateParam param);

	/**
	 * 获取配置下拉列表
	 * <p>
	 * 根据配置类型（必传）返回该类型下所有启用状态的配置选项，
	 * 每项包含配置 ID、名称、别名与配置值。
	 * </p>
	 *
	 * @param type 配置类型（必传），用于筛选指定类型的配置选项
	 * @return 返回指定类型的配置下拉列表数据
	 */
	@Get(value = PATH + "/{type}/dropDownList",description = "配置下拉列表")
	AbstractBaseResult<List<ConfigDropDownResult>> dropDownListResult(@PathVariable("type") String type);

	/**
	 * 根据配置名称获取配置
	 * <p>
	 * 配置名称为全局唯一键，用于前端按名称直接获取对应的配置信息。
	 * </p>
	 *
	 * @param name 配置名称，用于查询对应的配置记录
	 * @return 返回该配置名称对应的配置信息，包含配置 ID、名称、别名与配置值
	 */
	@Get(value = PATH + "/{name}/dropDown",description = "名称获取配置")
	AbstractBaseResult<ConfigDropDownResult> restDropDown(@PathVariable("name") String name);

//	===================== 单资源 =======================

	/** 单资源路径常量，用于指定具体配置 ID 的请求路径前缀 */
	String CURRENT_PATH = PATH + "/{id}";

	/**
	 * 获取配置详情
	 *
	 * @param id 配置 ID，用于查询指定配置的详细信息
	 * @return 返回配置详细信息，包含名称、类型、配置值、状态等完整属性
	 */
	@Get(value = CURRENT_PATH + "/get",description = "配置详情")
	@PreAuthorize("hasAuthority('system:config:get')")
	AbstractBaseResult<ConfigInfoVo> restGet(@PathVariable Long id);

	/**
	 * 删除配置
	 *
	 * @param id 配置 ID，指定需要删除的配置记录
	 * @return 无返回数据，仅返回操作结果状态
	 */
	@Post(value = CURRENT_PATH + "/remove",description = "配置删除")
	@PreAuthorize("hasAuthority('system:config:remove')")
	AbstractBaseResult<Void> restRemove(@PathVariable Long id);

	/**
	 * 更新配置信息（动态更新）
	 * <p>
	 * 仅更新传入的字段（名称、别名、类型、配置值），
	 * 未传入的字段保持原值不变，不参与更新。
	 * </p>
	 *
	 * @param id    配置 ID，指定需要更新的配置记录
	 * @param param 配置更新参数，仅需传入需要修改的字段
	 * @return 无返回数据，仅返回操作结果状态
	 */
	@Post(value = CURRENT_PATH + "/update",description = "配置更新")
	@PreAuthorize("hasAuthority('system:config:update')")
	AbstractBaseResult<Void> restUpdate(@PathVariable Long id, @RequestBody ConfigSaveUpdateParam param);

	/**
	 * 更新配置状态（启用/禁用）
	 *
	 * @param id    配置 ID，指定需要更新状态的配置记录
	 * @param param 状态参数，包含需要更新的目标状态值（启用/禁用）
	 * @return 无返回数据，仅返回操作结果状态
	 */
	@Post(value = CURRENT_PATH + "/updateStatus",description = "配置更新状态")
	@PreAuthorize("hasAuthority('system:config:update')")
	AbstractBaseResult<Void> restUpdateStatus(@PathVariable Long id, @RequestBody RequestParam<Status> param);

}
