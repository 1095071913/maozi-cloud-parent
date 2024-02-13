package com.maozi.base.param;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询入参
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchParam<D> extends AbstractBaseDtomain{

	/**
	 * 查询条件
	 */
	@Schema(description = "查询条件")
	private D search;

}
