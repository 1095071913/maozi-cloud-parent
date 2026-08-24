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
import java.util.List;
import java.util.Map;

/**
 * AI对话服务基础实现
 * <p>
 * 提供对话消息的通用转换能力，供 REST 层实现类继承使用。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/8/16 10:05
 */
@Service
public class ChatServiceImpl implements ChatService {

    /**
     * 将会话记忆中的消息对象转换为前端展示的消息结果列表
     * <p>
     * 从消息元数据中取出消息 ID 与创建时间，
     * 并收集用户消息与 AI 消息携带的媒体（图片）地址。
     * </p>
     *
     * @param messages 会话记忆中的消息列表
     * @return 前端展示的消息结果列表
     */
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
                        userMessage.getMedia().forEach(media -> images.add(media.getData().toString()));
                    }

                    if(message instanceof AssistantMessage assistantMessage){
                        assistantMessage.getMedia().forEach(media -> images.add(media.getData().toString()));
                    }

                    ChatMessageType chatMessageType = message.getMessageType() == MessageType.ASSISTANT ? ChatMessageType.AI : ChatMessageType.USER;
                    return new ChatItemResult(id, chatMessageType, message.getText(), images, createTime);

                })

                .toList();

    }

}
