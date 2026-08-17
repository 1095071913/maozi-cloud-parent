package com.maozi.ai.ai.config;

import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.content.Media;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/8/17 03:17
 */
@Data
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String messageType;

    private Map<String,Object> metadata = Map.of();

    private List<Media> media = List.of();

    private List<AssistantMessage.ToolCall> toolCalls = List.of();

    private String textContent;

    private List<ToolResponseMessage.ToolResponse> toolResponses = List.of();

    private Map<String, Object> params = Map.of();

}
