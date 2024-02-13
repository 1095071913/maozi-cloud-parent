package com.maozi.base.result;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EnumResult")
public class EnumResult extends AbstractBaseDtomain {
	
	@Schema(description = "枚举编码")
	private Integer value;
	
	@Schema(description = "描述")
	private String desc;

}
