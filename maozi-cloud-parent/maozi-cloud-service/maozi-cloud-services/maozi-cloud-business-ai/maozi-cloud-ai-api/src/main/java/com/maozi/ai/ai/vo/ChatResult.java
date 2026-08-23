package com.maozi.ai.ai.vo;

import com.maozi.ai.ai.enums.ChatType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pengjinlong
 * @since 2026/8/16 18:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "AI对话类型")
    private ChatType type;

    @Schema(description = "消息")
    private String message;

    @Schema(description = "数据")
    private Object data;

    public ChatResult(ChatType type, String message){
        this.type = type;
        this.message = message;
    }

}
