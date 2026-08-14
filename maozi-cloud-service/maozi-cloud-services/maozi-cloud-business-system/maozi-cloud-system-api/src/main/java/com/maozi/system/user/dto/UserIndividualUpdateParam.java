package com.maozi.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pengjinlong
 * @since 2026/8/13 17:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIndividualUpdateParam implements Serializable {

    /** 序列化版本号 */
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "头像")
    private String icon;

    @Schema(description = "旧密码")
    private String password;

    @Schema(description = "新密码")
    private String newPassword;

}
