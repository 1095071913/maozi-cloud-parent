package com.maozi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author pengjinlong
 * @since 2026/6/16 16:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserInfo implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 客户端ID
     */
    private Long clientId;

}
