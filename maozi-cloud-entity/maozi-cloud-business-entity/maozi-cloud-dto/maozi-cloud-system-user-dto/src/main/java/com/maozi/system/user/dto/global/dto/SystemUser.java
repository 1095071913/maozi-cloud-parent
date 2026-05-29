package com.maozi.system.user.dto.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

	@Schema(description = "标识")
	private Long id;
	
	@Schema(description = "账号")
	private String username;
	
	@Schema(description = "名称")
	private String name;
	
	@Schema(description = "头像")
	private String icon;
	
}
