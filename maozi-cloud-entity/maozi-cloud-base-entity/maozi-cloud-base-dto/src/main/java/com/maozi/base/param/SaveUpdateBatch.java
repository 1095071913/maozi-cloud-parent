package com.maozi.base.param;

import com.maozi.base.AbstractBaseDtomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveUpdateBatch extends AbstractBaseDtomain {
	
	@ApiModelProperty("标识")
	private Long id;
	
}