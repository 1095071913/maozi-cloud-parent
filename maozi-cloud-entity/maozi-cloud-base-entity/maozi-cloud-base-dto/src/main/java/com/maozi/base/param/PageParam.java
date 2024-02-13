package com.maozi.base.param;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam<D> extends AbstractBaseDtomain {

	@Schema(description = "页数",example = "1")
	private Long current=1L;
	
	@Schema(description = "每页数量",example = "10")
	private Long size=10L;
	
	@Valid
	@Schema(description = "查询条件")
	private D data;
	
}
