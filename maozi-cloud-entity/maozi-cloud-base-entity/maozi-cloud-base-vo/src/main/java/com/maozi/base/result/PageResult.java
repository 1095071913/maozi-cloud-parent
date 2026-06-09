package com.maozi.base.result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 * <p>
 * 通用分页数据返回结构，包含页码、每页数量、数据总数和当前页数据列表。
 * 支持泛型以适配不同的业务数据类型。
 * </p>
 *
 * @param <D> 数据类型
 * @author maozi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<D> implements Serializable {

	/** 序列化标识 */
	@Serial
    private static final long serialVersionUID = 1L;

	/** 当前页码 */
	@Schema(description = "页数")
	private Long current;

	/** 每页数量 */
	@Schema(description = "每页数量")
	private Long size;

	/** 数据总数 */
	@Schema(description = "数据总数")
	private Long total;

	/** 当前页数据列表 */
	@Valid
	private List<D> data;

}
