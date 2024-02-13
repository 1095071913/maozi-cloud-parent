package com.maozi.base.param.plugin;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeParam extends AbstractBaseDtomain {
	
	@Schema(description = "开始时间 时间戳")
	private Long startTime;
	
	@Schema(description = "结束时间 时间戳")
	private Long endTime;

}
