package com.maozi.ai.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pengjinlong
 * @since 2026/8/17 09:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationRecordListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** AI聊天会话ID */
    @Schema(description = "AI聊天会话ID")
    private Long id;

    /** 标题 */
    @Schema(description = "标题")
    private String title;

}
