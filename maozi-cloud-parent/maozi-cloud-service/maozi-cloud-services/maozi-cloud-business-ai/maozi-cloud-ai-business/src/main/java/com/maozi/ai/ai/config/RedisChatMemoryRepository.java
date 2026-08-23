package com.maozi.ai.ai.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.maozi.ai.ai.constant.ChatMessageConstant;
import com.maozi.common.CollectionUtil;
import com.maozi.common.JacksonUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.redis.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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

    /** 消息顺序索引 key 后缀：ZSET，member = 消息雪花 ID，score = 写入时刻毫秒时间戳 */
    private final static String AI_CHAT_MEMORY_ORDER_KEY_SUFFIX = ":order";

    /** 用户对话记录索引 key 后缀：ZSET，member = 消息雪花 ID，score = 写入时刻毫秒时间戳，仅记录 USER / ASSISTANT 消息 */
    private final static String AI_CHAT_MEMORY_RECORD_KEY_SUFFIX = ":record";

    @NotNull
    @Override
    public List<String> findConversationIds() {

        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + "*";

        List<String> conversationIds = CollectionUtil.newArrayList();
        try (Cursor<String> cursor = redisClient.scan(ScanOptions.scanOptions().match(aiChatMemoryKey).count(1000).build())) {
            cursor.forEachRemaining(key -> {
                if (key.endsWith(AI_CHAT_MEMORY_ORDER_KEY_SUFFIX) || key.endsWith(AI_CHAT_MEMORY_RECORD_KEY_SUFFIX)) {
                    return;
                }
                conversationIds.add(StrUtil.replace(key, AI_CHAT_MEMORY_KEY, ""));
            });
        }

        return conversationIds;

    }

    @NotNull
    @Override
    public List<Message> findByConversationId(@NotNull String conversationId) {
        return findMessageByIndexSuffix(conversationId, AI_CHAT_MEMORY_ORDER_KEY_SUFFIX, Double.NEGATIVE_INFINITY);
    }

    @NotNull
    @Override
    public List<Message> findRecordByConversationId(@NotNull String conversationId) {
        return findMessageByIndexSuffix(conversationId, AI_CHAT_MEMORY_RECORD_KEY_SUFFIX, Double.NEGATIVE_INFINITY);
    }

    @NotNull
    @Override
    public List<Message> findRecordAfterTimestamp(@NotNull String conversationId, Long timestamp) {
        return findMessageByIndexSuffix(conversationId, AI_CHAT_MEMORY_RECORD_KEY_SUFFIX, timestamp + 1);
    }

    private List<Message> findMessageByIndexSuffix(String conversationId, String indexKeySuffix, double minScore) {

        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;

        Set<String> messageIds = redisClient.boundZSetOps(aiChatMemoryKey + indexKeySuffix).rangeByScore(minScore, Double.POSITIVE_INFINITY);
        if(ObjectUtil.isNullEmpty(messageIds)){
            return CollectionUtil.newArrayList();
        }

        List<String> messages = redisClient.<String, String>boundHashOps(aiChatMemoryKey).multiGet(messageIds);
        if(ObjectUtil.isNullEmpty(messages)){
            return CollectionUtil.newArrayList();
        }

        return messages.stream()
                .filter(Objects::nonNull)
                .map(this::jsonToMessage)
                .toList();

    }

    @Override
    public void saveAll(@NotNull String conversationId, @NotNull List<Message> messages) {

        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;

        deleteByConversationId(conversationId);

        Map<String, String> messageMap = CollectionUtil.newHashMap();
        Set<ZSetOperations.TypedTuple<String>> orderTuples = CollectionUtil.newHashSet();
        Set<ZSetOperations.TypedTuple<String>> recordTuples = CollectionUtil.newHashSet();
        messages.forEach(message -> {

            Map<String, Object> metadata = message.getMetadata();
            String messageId = (String) metadata.get(ChatMessageConstant.ID);
            String createTime = (String) metadata.get(ChatMessageConstant.CREATE_TIME);
            if(ObjectUtil.isNullEmpty(messageId)){
                createTime = String.valueOf(System.currentTimeMillis());
                messageId = String.valueOf(IdWorker.getId());
                metadata.put(ChatMessageConstant.ID, messageId);
                metadata.put(ChatMessageConstant.CREATE_TIME, createTime);
            }

            messageMap.put(messageId, Objects.requireNonNull(messageToJson(message)));
            orderTuples.add(ZSetOperations.TypedTuple.of(messageId, Double.parseDouble(createTime)));

            MessageType messageType = message.getMessageType();
            if (MessageType.USER == messageType || MessageType.ASSISTANT == messageType) {
                recordTuples.add(ZSetOperations.TypedTuple.of(messageId, Double.parseDouble(createTime)));
            }

        });

        BoundHashOperations<String, String, String> hashOps = redisClient.boundHashOps(aiChatMemoryKey);
        hashOps.putAll(messageMap);

        redisClient.boundZSetOps(aiChatMemoryKey + AI_CHAT_MEMORY_ORDER_KEY_SUFFIX).add(orderTuples);

        if(!recordTuples.isEmpty()){
            redisClient.boundZSetOps(aiChatMemoryKey + AI_CHAT_MEMORY_RECORD_KEY_SUFFIX).add(recordTuples);
        }

    }

    @Override
    public void deleteByConversationId(@NotNull String conversationId) {
        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;
        redisClient.delete(List.of(aiChatMemoryKey, aiChatMemoryKey + AI_CHAT_MEMORY_ORDER_KEY_SUFFIX, aiChatMemoryKey + AI_CHAT_MEMORY_RECORD_KEY_SUFFIX));
    }

    @Override
    public void deleteByMessageId(@NotNull String conversationId, @NotNull String messageId) {
        String aiChatMemoryKey = AI_CHAT_MEMORY_KEY + conversationId;
        redisClient.<String, String>boundHashOps(aiChatMemoryKey).delete(messageId);
        redisClient.boundZSetOps(aiChatMemoryKey + AI_CHAT_MEMORY_ORDER_KEY_SUFFIX).remove(messageId);
        redisClient.boundZSetOps(aiChatMemoryKey + AI_CHAT_MEMORY_RECORD_KEY_SUFFIX).remove(messageId);
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
