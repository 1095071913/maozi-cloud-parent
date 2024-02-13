package com.maozi.oauth.token.dto.platform.dto;

import com.maozi.base.AbstractBaseDtomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthToken extends AbstractBaseDtomain {

	@Schema(description = "通行令牌")
	private String accessToken;
	
	@Schema(description = "刷新令牌")
	private String refreshToken;
	
	@Schema(description = "通行令牌 过期时间")
	private Long accessTokenExpiresTime;
	
	@Schema(description = "刷新令牌 过期时间")
	private Long refreshTokenExpiresTime;
	
}
