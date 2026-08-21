package com.maozi.ai.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pengjinlong
 * @since 2026/8/16 10:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "消息")
    @NotEmpty(message = "消息不能为空")
    private String message;

    @Schema(description = "对话ID")
    @NotNull(message = "对话ID不能为空")
    private Long conversationId;

    @Schema(description = "提示词配置")
    private String promptConfig;

}
