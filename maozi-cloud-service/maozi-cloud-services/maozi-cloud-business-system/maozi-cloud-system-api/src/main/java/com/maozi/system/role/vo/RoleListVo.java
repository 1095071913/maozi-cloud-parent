package com.maozi.system.role.vo;

import com.maozi.base.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色列表视图对象
 * <p>
 * 用于角色列表展示时的视图对象，包含角色的基本信息，
 * 如ID、名称、描述、更新时间和状态等。
 * </p>
 */
@Data
public class RoleListVo implements Serializable {

	/** 序列化版本号 */
	@Serial
    private static final long serialVersionUID = 1L;

	/** 角色ID */
	@Schema(description = "标识")
	private Long id;

	/** 角色名称 */
	@Schema(description = "名称")
	private String name;

	/** 角色描述 */
	@Schema(description = "描述")
	private String description;

	/** 角色最后更新时间（时间戳） */
	@Schema(description = "更新时间")
	private Long updateTime;

	/** 角色状态（启用/禁用） */
	@Schema(description = "状态")
	private Status status;

}
