package com.maozi.ai.ai.api.impl;

import com.maozi.ai.ai.api.ChatService;
import com.maozi.ai.ai.constant.ChatMessageConstant;
import com.maozi.ai.ai.enums.ChatMessageType;
import com.maozi.ai.ai.vo.ChatItemResult;
import com.maozi.common.CollectionUtil;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/8/16 10:05
 */
@Service
public class ChatServiceImpl implements ChatService {

    protected List<ChatItemResult> convertMessageList(List<Message> messages){

        return messages.stream()
                .map(message -> {

                    Map<String, Object> metadata = message.getMetadata();
                    String id = metadata.get(ChatMessageConstant.ID).toString();
                    LocalDateTime createTime = Instant.ofEpochMilli(Long.parseLong(metadata.get(ChatMessageConstant.CREATE_TIME).toString()))
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    List<String> images = CollectionUtil.newArrayList();
                    if(message instanceof UserMessage userMessage){
                        userMessage.getMedia().forEach(media -> images.add(Base64.getEncoder().encodeToString(media.getDataAsByteArray())));
                    }

                    if(message instanceof AssistantMessage assistantMessage){
                        assistantMessage.getMedia().forEach(media -> images.add(Base64.getEncoder().encodeToString(media.getDataAsByteArray())));
                    }

                    ChatMessageType chatMessageType = message.getMessageType() == MessageType.ASSISTANT ? ChatMessageType.AI : ChatMessageType.USER;
                    return new ChatItemResult(id, chatMessageType, message.getText(), images, createTime);

                })

                .toList();

    }

}
