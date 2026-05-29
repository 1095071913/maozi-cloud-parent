package com.maozi.base.result;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<D> implements Serializable {
	
	@Serial
    private static final long serialVersionUID = 1L;
	
	@Schema(description = "页数")
	private Long current;
	
	@Schema(description = "每页数量")
	private Long size;

	@Schema(description = "数据总数")
	private Long total;
	
	@Valid
	private List<D> data;

}