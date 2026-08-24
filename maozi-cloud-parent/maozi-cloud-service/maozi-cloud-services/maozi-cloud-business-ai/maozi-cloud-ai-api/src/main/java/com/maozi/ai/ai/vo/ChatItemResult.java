package com.maozi.ai.ai.vo;

import com.maozi.ai.ai.enums.ChatMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话消息结果
 * <p>
 * 前端展示的单条对话消息，包含消息 ID、消息类型（用户/AI）、
 * 消息内容、携带的图片地址以及创建时间。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/8/17 10:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatItemResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private String id;

    @Schema(description = "AI对话消息类型")
    private ChatMessageType type;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "图片列表地址")
    private List<String> images;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
