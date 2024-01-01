package com.maozi.oauth.token.dto.platform.param;

import com.maozi.base.AbstractBaseDtomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ClientUserParam extends AbstractBaseDtomain {
	
	private Long clientId;
	
	private String username;

}
