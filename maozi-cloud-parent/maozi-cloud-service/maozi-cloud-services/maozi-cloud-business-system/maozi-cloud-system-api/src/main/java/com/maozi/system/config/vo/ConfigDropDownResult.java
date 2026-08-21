package com.maozi.system.config.vo;

import com.maozi.base.result.DropDownResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 配置下拉选择结果
 * <p>
 * 用于配置下拉列表接口的返回结果对象。
 * 继承自通用下拉结果基类，额外包含配置别名与配置值信息，
 * 以支持前端在下拉选择时直接展示或使用配置内容。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDropDownResult extends DropDownResult implements Serializable {

	/** 序列化版本号 */
	@Serial
	private static final long serialVersionUID = 1L;

	/** 配置别名 */
	@Schema(description = "别名")
	private String alias;

	/** 配置值 */
	@Schema(description = "配置值")
	private String value;

}
