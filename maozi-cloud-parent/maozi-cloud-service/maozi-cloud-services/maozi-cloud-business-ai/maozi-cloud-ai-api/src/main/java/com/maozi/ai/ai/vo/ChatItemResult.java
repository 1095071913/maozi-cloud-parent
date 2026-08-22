package com.maozi.ai.ai.vo;

import com.maozi.ai.ai.enums.ChatMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
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

    @Schema(description = "AI对话类型")
    private ChatMessageType type;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "Base64图片列表")
    private List<String> images;

}
