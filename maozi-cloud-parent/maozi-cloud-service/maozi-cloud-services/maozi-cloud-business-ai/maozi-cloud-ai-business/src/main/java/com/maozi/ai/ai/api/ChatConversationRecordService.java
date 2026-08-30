package com.maozi.ai.ai.api;

/**
 * AI交互会话业务接口
 * <p>
 * 定义AI交互会话领域对内暴露的业务方法契约，
 * 供本模块内部其他业务（如AI对话流程）调用。
 * </p>
 */
public interface ChatConversationRecordService {

    /**
     * 校验会话是否存在
     *
     * @param conversationId 会话 ID
     */
    void has(Long conversationId);

}
