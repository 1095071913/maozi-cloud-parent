package com.maozi.base.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 请求入参
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestParam<D> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

	/**
	 * 数据
	 */
	@Valid
	@Schema(description = "数据")
	private D data;

}
