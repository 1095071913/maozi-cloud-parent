package com.maozi.base.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveUpdateBatch implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
	
	@Schema(description = "标识")
	private Long id;
	
}