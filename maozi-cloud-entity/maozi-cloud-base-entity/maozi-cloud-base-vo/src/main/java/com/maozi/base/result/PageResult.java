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
 * 分页查询结果封装类
 * <p>
 * 通用的分页数据返回结构，用于将分页查询的结果统一封装后返回给前端。
 * 包含当前页码、每页数量、数据总条数和当前页的数据列表。
 * </p>
 * <p>
 * 使用泛型 {@code <D>} 来适配不同的业务数据类型（如用户VO、订单VO等），
 * 避免为每种业务类型重复定义分页结构。
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 *   PageResult&lt;UserVO&gt; result = new PageResult&lt;&gt;(1L, 10L, 100L, userList);
 * </pre>
 * </p>
 * <p>
 * 使用 Lombok {@code @Data} 自动生成 getter/setter/toString/equals/hashCode 方法；
 * {@code @NoArgsConstructor} 生成无参构造方法；
 * {@code @AllArgsConstructor} 生成全参构造方法（参数顺序：current, size, total, data）。
 * </p>
 *
 * @param <D> 当前页数据的类型，通常为业务VO类
 * @author maozi
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<D> implements Serializable {

	/** 序列化版本号，用于确保序列化与反序列化的兼容性 */
	@Serial
    private static final long serialVersionUID = 1L;

	/** 当前页码，从1开始计数 */
	@Schema(description = "页数")
	private Long current;

	/** 每页显示的数据条数 */
	@Schema(description = "每页数量")
	private Long size;

	/** 符合查询条件的数据总条数，用于前端计算总页数 */
	@Schema(description = "数据总数")
	private Long total;

	/**
	 * 当前页的数据列表。
	 * <p>
	 * 使用 {@code @Valid} 注解标记，表示在参数校验时会级联校验列表中每个元素的约束注解
	 * （如 {@code @NotNull}、{@code @Size} 等）。
	 * </p>
	 */
	@Valid
	private List<D> data;

}
