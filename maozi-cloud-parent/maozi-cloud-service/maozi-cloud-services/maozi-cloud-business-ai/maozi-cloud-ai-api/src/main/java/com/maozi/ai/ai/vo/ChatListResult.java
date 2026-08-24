package com.maozi.ai.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI对话消息列表返回结果
 * <p>
 * 包含会话是否正在对话中以及该会话的用户对话消息列表。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/8/22 20:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否对话中")
    private Boolean isLocked;

    @Schema(description = "对话内容")
    private List<ChatItemResult> items;

}
