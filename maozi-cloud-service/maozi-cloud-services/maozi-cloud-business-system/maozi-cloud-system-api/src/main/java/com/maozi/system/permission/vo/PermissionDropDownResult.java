package com.maozi.system.permission.vo;

import com.maozi.base.result.DropDownResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限下拉选择结果
 * <p>
 * 用于权限下拉列表选择时返回的结果对象。
 * 继承自通用下拉结果基类，额外包含上级权限ID和层级深度信息，
 * 以支持树形结构的权限选择。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDropDownResult extends DropDownResult {

	/** 上级权限ID，用于构建权限树形结构 */
	@Schema(description = "上级ID")
	private Long parentId;

	/** 权限在树形结构中的层级深度 */
	@Schema(description = "深度")
	private Integer level;

}
