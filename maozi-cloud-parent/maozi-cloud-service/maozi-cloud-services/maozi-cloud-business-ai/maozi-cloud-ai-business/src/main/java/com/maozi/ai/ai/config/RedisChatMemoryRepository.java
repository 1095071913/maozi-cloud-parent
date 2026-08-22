package com.maozi.ai.ai.config;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.maozi.common.CollectionUtil;
import com.maozi.common.JacksonUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.redis.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author pengjinlong
 * @since 2026/8/16 20:06
 */
@Component
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    @Resource
    private StringRedisTemplate redisClient;

    private final static String AI_CHAT_MEMORY_KEY = RedisUtil.REDIS_KEY_PREFIX + "chat:memory:";

    @NotNull
    @Override
    public List<String> findConversationIds() {

        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + "*";
        Set<String> keys = redisClient.keys(aiChatMemoryKey);
        if(ObjectUtil.isNullEmpty(keys)){
            return CollectionUtil.newArrayList();
        }

        return StreamUtil.of(keys)
                .map(key -> StrUtil.replace(key, AI_CHAT_MEMORY_KEY, ""))
                .toList();

    }

    @NotNull
    @Override
    public List<Message> findByConversationId(@NotNull String conversationId) {

        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;

        List<String> messages = redisClient.boundListOps(aiChatMemoryKey).range(0, -1);
        if(ObjectUtil.isNullEmpty(messages)){
            return CollectionUtil.newArrayList();
        }

        return CollStreamUtil.toList(messages,this::jsonToMessage);

    }

    @Override
    public void saveAll(@NotNull String conversationId, @NotNull List<Message> messages) {

        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;

        deleteByConversationId(conversationId);

        BoundListOperations<String, String> listOps = redisClient.boundListOps(aiChatMemoryKey);
        messages.forEach(message -> listOps.rightPush(Objects.requireNonNull(messageToJson(message))));


    }

    @Override
    public void deleteByConversationId(@NotNull String conversationId) {
        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;
        redisClient.delete(aiChatMemoryKey);
    }

    public String messageToJson(Message message) {

        ChatMessage chatMessage = CglibUtil.copy(message, ChatMessage.class);
        chatMessage.setMessageType(message.getMessageType().getValue().toLowerCase());
        chatMessage.setTextContent(message.getText());

        if(message instanceof AssistantMessage assistantMessage){
            chatMessage.setToolCalls(assistantMessage.getToolCalls());
        }

        if(message instanceof ToolResponseMessage toolResponseMessage){
            chatMessage.setToolResponses(toolResponseMessage.getResponses());
        }

        return JacksonUtil.objectToJson(chatMessage);

    }

    public Message jsonToMessage(String json){

        ChatMessage chatMessage = JacksonUtil.jsonToObject(json, ChatMessage.class);
        if(ObjectUtil.isNullEmpty(chatMessage)){
            throw new RuntimeException("message data conversion error");
        }

        String messageTypeString = chatMessage.getMessageType();
        if(ObjectUtil.isNullEmpty(messageTypeString)){
            throw new RuntimeException("messageType is null");
        }

        MessageType messageType = MessageType.fromValue(messageTypeString);
        switch (messageType) {
            case SYSTEM -> {
                return new SystemMessage(chatMessage.getTextContent());
            }
            case USER ->  {
                return UserMessage.builder()
                        .text(chatMessage.getTextContent())
                        .metadata(chatMessage.getMetadata())
                        .media(chatMessage.getMedia())
                        .build();
            }
            case ASSISTANT -> {
                return new AssistantMessage(chatMessage.getTextContent(), chatMessage.getMetadata(), chatMessage.getToolCalls(),chatMessage.getMedia());
            }
            case TOOL -> {
                return new ToolResponseMessage(chatMessage.getToolResponses(),chatMessage.getMetadata());
            }
        }

        throw new RuntimeException("message data conversion error");

    }

}
