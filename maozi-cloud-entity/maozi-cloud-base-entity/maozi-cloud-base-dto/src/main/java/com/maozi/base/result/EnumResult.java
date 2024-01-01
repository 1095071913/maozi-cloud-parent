package com.maozi.base.result;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumResult extends AbstractBaseDtomain {
	
	@ApiModelProperty(value = "枚举编码")
	private Integer value;
	
	@ApiModelProperty(value = "描述")
	private String desc;

}
