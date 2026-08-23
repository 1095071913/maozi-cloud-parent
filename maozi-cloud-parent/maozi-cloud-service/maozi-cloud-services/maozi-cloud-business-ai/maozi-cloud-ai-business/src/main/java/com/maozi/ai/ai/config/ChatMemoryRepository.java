package com.maozi.ai.ai.config;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * @author pengjinlong
 * @since 2026/8/23 04:16
 */
public interface ChatMemoryRepository extends org.springframework.ai.chat.memory.ChatMemoryRepository {

    void deleteByMessageId(String conversationId, String messageId);

    /**
     * 查询会话的用户对话记录（仅 USER / ASSISTANT 消息，按时间正序）
     *
     * @param conversationId 会话 ID
     * @return 对话记录消息列表
     */
    List<Message> findRecordByConversationId(String conversationId);

    /**
     * 查询会话中写入时间晚于指定时间戳的用户对话消息（仅 USER / ASSISTANT 消息，按时间正序）
     *
     * @param conversationId 会话 ID
     * @param timestamp 写入时刻毫秒时间戳（不包含等于该时间戳的消息）
     * @return 对话记录消息列表
     */
    List<Message> findRecordAfterTimestamp(String conversationId, Long timestamp);

}
