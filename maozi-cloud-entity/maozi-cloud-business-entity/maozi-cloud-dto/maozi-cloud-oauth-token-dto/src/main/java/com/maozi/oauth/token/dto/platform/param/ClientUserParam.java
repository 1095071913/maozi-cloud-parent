package com.maozi.oauth.token.dto.platform.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ClientUserParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
	
	private Long clientId;
	
	private String username;

}
