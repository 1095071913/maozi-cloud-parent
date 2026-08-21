package com.maozi.system.config.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maozi.base.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局配置详情展示对象
 * <p>
 * 用于全局配置详情接口的返回数据封装。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigInfoVo implements Serializable {

	/** 序列化版本号 */
	@Serial
	private static final long serialVersionUID = 1L;

	/** 配置ID */
	@JsonIgnore
	@Schema(hidden = true)
	private Long id;

	/** 配置名称 */
	@Schema(description = "名称")
	private String name;

	/** 配置别名 */
	@Schema(description = "别名")
	private String alias;

	/** 配置类型 */
	@Schema(description = "类型")
	private String type;

	/** 配置值 */
	@Schema(description = "配置值")
	private String value;

	/** 状态（启用/禁用） */
	@Schema(description = "状态")
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	private Status status;

}
