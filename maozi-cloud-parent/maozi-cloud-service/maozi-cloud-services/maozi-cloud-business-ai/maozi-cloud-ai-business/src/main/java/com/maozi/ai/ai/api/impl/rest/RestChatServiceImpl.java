package com.maozi.ai.ai.api.impl.rest;

import com.maozi.ai.ai.api.ChatConversationRecordService;
import com.maozi.ai.ai.api.impl.ChatServiceImpl;
import com.maozi.ai.ai.api.rest.RestChatService;
import com.maozi.ai.ai.dto.ChatParam;
import com.maozi.ai.ai.enums.ChatMessageType;
import com.maozi.ai.ai.enums.ChatType;
import com.maozi.ai.ai.vo.ChatListResult;
import com.maozi.ai.ai.vo.ChatResult;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.redis.utils.RedisUtil;
import com.maozi.service.api.annotation.RemoteResource;
import com.maozi.service.api.annotation.RestService;
import com.maozi.system.config.api.rpc.RpcConfigService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.data.redis.core.BoundHashOperations;
import reactor.core.publisher.Flux;

import java.util.List;

@RestService
@RequiredArgsConstructor
public class RestChatServiceImpl extends ChatServiceImpl implements RestChatService {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory;

    @RemoteResource
    private RpcConfigService rpcConfigService;

    @Resource(name = "chatConversationRecordServiceImpl")
    private ChatConversationRecordService  chatConversationRecordService;

    private static final String AI_CHAT_STOP_KEY = RedisUtil.REDIS_KEY_PREFIX + "chat:status";

    @Override
    public Flux<AbstractBaseResult<ChatResult>> chat(ChatParam param) {

        Long conversationId = param.getConversationId();
        chatConversationRecordService.has(conversationId);

        ChatClient.ChatClientRequestSpec chatClientRequest = chatClient.prompt();

        String promptConfig = param.getPromptConfig();
        if(ObjectUtil.isNotNullEmpty(promptConfig)){
            promptConfig = rpcConfigService.rpcGetValueByKey(promptConfig).getResultDataThrowError();
            if(ObjectUtil.isNotNullEmpty(promptConfig)){
                chatClientRequest.system(promptConfig);
            }
        }

        BoundHashOperations<String, Object, Object> hashOps = RedisUtil.getRedisClient().boundHashOps(AI_CHAT_STOP_KEY);

        StringBuilder aiChatOutputMessage = new StringBuilder();
        final String conversationIdFinal = conversationId.toString();
        return chatClientRequest
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationIdFinal))
                .user(param.getMessage())
                .stream()
                .chatResponse()

                // 请求前处理
                .doFirst(() -> hashOps.put(conversationIdFinal, Boolean.TRUE.toString()))

                // 异常时处理
                .doOnError(throwable -> hashOps.delete(conversationIdFinal))

                // 请求结束后处理
                .doOnComplete(() -> hashOps.delete(conversationIdFinal))

                .doOnCancel(() -> {
                    chatMemory.add(conversationIdFinal,new AssistantMessage(aiChatOutputMessage.toString()));
                })

                // 是否打断对话流通道
                .takeWhile(chatResponse -> ObjectUtil.isNotNullEmpty(hashOps.get(conversationIdFinal)))

                // 每次获取到流数据后的处理
                .map(chatResponse -> {
                    String aiChat = chatResponse.getResult().getOutput().getText();
                    aiChatOutputMessage.append(aiChat);
                    return (AbstractBaseResult<ChatResult>) ResultUtil.success(new ChatResult(ChatType.OUTPUT, aiChat));
                })

                // 告诉前端对话流通道已结束 可以关闭连接
                .concatWith(Flux.just(ResultUtil.success(ChatResult.builder().type(ChatType.FINISH).build())));

    }

    @Override
    public AbstractBaseResult<List<ChatListResult>> list(String conversationId) {

        List<Message> messages = chatMemory.get(conversationId);
        if(CollectionUtil.isEmpty(messages)){
            return ResultUtil.success(CollectionUtil.newArrayList());
        }

        List<ChatListResult> responses = messages.stream()
                .filter(message -> message.getMessageType() == MessageType.ASSISTANT || message.getMessageType() == MessageType.USER)
                .map(message -> new ChatListResult(message.getMessageType() == MessageType.ASSISTANT ? ChatMessageType.AI : ChatMessageType.USER, message.getText()))
                .toList();

        return ResultUtil.success(responses);

    }

    @Override
    public AbstractBaseResult<Void> stop(String conversationId) {
        RedisUtil.getRedisClient().boundHashOps(AI_CHAT_STOP_KEY).delete(conversationId);
        return ResultUtil.success();
    }

}
