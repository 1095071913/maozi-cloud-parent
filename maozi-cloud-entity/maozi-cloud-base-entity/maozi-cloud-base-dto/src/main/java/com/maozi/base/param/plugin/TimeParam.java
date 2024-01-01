package com.maozi.base.param.plugin;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeParam extends AbstractBaseDtomain {
	
	@ApiModelProperty("开始时间 时间戳")
	private Long startTime;
	
	@ApiModelProperty("结束时间 时间戳")
	private Long endTime;

}
