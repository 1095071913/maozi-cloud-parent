package com.maozi.oauth.token.dto.platform.param;

import io.swagger.annotations.ApiModelProperty;
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
	
	@ApiModelProperty("用户账号")
	@NotNull(message = "用户账号不能为空")
	private String username;
	
	@ApiModelProperty("用户密码")
	@NotNull(message = "用户密码不能为空")
	private String password;

}
