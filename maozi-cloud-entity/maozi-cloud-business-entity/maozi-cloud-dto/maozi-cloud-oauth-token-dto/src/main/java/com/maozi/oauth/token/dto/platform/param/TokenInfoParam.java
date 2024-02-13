package com.maozi.oauth.token.dto.platform.param;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class TokenInfoParam extends ClientParam {
	
	@Schema(description = "用户账号")
	@NotNull(message = "用户账号不能为空")
	private String username;
	
	@Schema(description = "用户密码")
	@NotNull(message = "用户密码不能为空")
	private String password;

}
