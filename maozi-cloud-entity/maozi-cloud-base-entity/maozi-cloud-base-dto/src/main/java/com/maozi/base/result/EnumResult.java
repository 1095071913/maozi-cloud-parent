package com.maozi.base.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EnumResult")
public class EnumResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
	
	@Schema(description = "枚举编码")
	private Integer value;
	
	@Schema(description = "描述")
	private String desc;

}
