package com.maozi.base.param.plugin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
	
	@Schema(description = "开始时间 时间戳")
	private Long startTime;
	
	@Schema(description = "结束时间 时间戳")
	private Long endTime;

}
