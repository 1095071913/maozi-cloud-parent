package com.maozi.base.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam<D> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "页数",defaultValue = "1")
	private Long current = 1L;
	
	@Schema(description = "每页数量",defaultValue = "10")
	private Long size = 10L;
	
	@Valid
	@Schema(description = "查询条件")
	private D data;
	
}
