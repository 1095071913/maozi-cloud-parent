package com.maozi.oauth.token.dto.platform.dto;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthToken extends AbstractBaseDtomain {

	@ApiModelProperty("通行令牌")
	private String accessToken;
	
	@ApiModelProperty("刷新令牌")
	private String refreshToken;
	
	@ApiModelProperty("通行令牌 过期时间")
	private Long accessTokenExpiresTime;
	
	@ApiModelProperty("刷新令牌 过期时间")
	private Long refreshTokenExpiresTime;
	
}
