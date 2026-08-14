package com.maozi.system.user.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengjinlong
 * @since 2026/8/13 19:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OauthUserInfoResult implements Serializable {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户密码")
    private String password;

    @Schema(description = "权限列表")
    private List<String> authorities;

}
