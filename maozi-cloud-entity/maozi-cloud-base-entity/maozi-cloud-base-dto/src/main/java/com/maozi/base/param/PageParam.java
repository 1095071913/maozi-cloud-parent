package com.maozi.base.param;

import javax.validation.Valid;

import com.maozi.base.AbstractBaseDtomain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam<D> extends AbstractBaseDtomain{

	@ApiModelProperty(value = "页数",example = "1")
	private Long current=1L;
	
	@ApiModelProperty(value = "每页数量",example = "10")
	private Long size=10L;
	
	@Valid
	@ApiModelProperty("查询条件")
	private D data;
	
}
