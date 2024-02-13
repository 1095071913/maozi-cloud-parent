package com.maozi.base.param;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveUpdateBatch extends AbstractBaseDtomain {
	
	@Schema(description = "标识")
	private Long id;
	
}