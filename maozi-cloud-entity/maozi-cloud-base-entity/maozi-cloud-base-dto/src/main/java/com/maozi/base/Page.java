package com.maozi.base;

import javax.validation.Valid;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<D> extends AbstractBaseDtomain{

	@ApiModelProperty(value = "页数",example = "1")
	private Long current=1L;
	
	@ApiModelProperty(value = "每页数量",example = "10")
	private Long size=10L;

	@ApiModelProperty(value = "数据总数",hidden = true)
	private Long total;
	
	@Valid
	private D data;
	
}
