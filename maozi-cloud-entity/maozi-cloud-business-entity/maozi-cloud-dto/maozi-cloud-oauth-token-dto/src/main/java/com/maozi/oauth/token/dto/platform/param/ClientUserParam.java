package com.maozi.oauth.token.dto.platform.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 客户端用户参数
 * <p>
 * 用于客户端用户关联查询的请求参数，包含客户端 ID 和用户名。
 * </p>
 *
 * @author maozi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ClientUserParam implements Serializable {

    /** 序列化版本 ID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 客户端 ID */
	private String clientId;

    /** 用户名 */
	private String username;

}
