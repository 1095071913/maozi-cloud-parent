package com.maozi.ai.ai.api.impl.rest;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.maozi.ai.ai.api.ChatConversationRecordService;
import com.maozi.ai.ai.api.impl.ChatServiceImpl;
import com.maozi.ai.ai.api.rest.RestChatService;
import com.maozi.ai.ai.config.ChatMemoryRepository;
import com.maozi.ai.ai.dto.ChatGenerateImageParam;
import com.maozi.ai.ai.dto.ChatParam;
import com.maozi.ai.ai.enums.ChatType;
import com.maozi.ai.ai.vo.ChatGenerateImageResult;
import com.maozi.ai.ai.vo.ChatItemResult;
import com.maozi.ai.ai.vo.ChatListResult;
import com.maozi.ai.ai.vo.ChatResult;
import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.ValidatorUtil;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.redis.utils.RedisUtil;
import com.maozi.service.api.annotation.RemoteResource;
import com.maozi.service.api.annotation.RestService;
import com.maozi.system.config.api.rpc.RpcConfigService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestService
@RequiredArgsConstructor
public class RestChatServiceImpl extends ChatServiceImpl implements RestChatService {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory;

    private final ImageModel imageModel;

    private final ChatMemoryRepository chatMemoryRepository;

    @Value("${spring.ai.dashscope.chat.options.text_image_model:}")
    private String imageModelName;

    @RemoteResource
    private RpcConfigService rpcConfigService;

    @Resource(name = "chatConversationRecordServiceImpl")
    private ChatConversationRecordService  chatConversationRecordService;

    public static final String AI_CHAT_STOP_KEY = RedisUtil.REDIS_KEY_PREFIX + "chat:status:";

    public static final String AI_CHAT_GENERATE_IMAGE_STOP_KEY = RedisUtil.REDIS_KEY_PREFIX + "chat:generate:image:status:";

    @Override
    @SneakyThrows
    public Flux<AbstractBaseResult<ChatResult>> chat(List<MultipartFile> files, ChatParam param) {

        ValidatorUtil.validate(param);

        Long conversationId = param.getConversationId();
        chatConversationRecordService.has(conversationId);

        UserMessage.Builder userMessageBuilder = UserMessage.builder().text(param.getMessage());

        boolean hasImage = false;
        if(ObjectUtil.isNotNullEmpty(files)){
            List<Media> medias = CollectionUtil.newArrayList();
            files.forEach(file -> {
                String fileType = file.getContentType();
                if(ObjectUtil.isNotNullEmpty(fileType) && fileType.startsWith("image/")){
                    medias.add(new Media(MimeTypeUtils.parseMimeType(Objects.requireNonNull(fileType)), file.getResource()));
                }
            });
            if(ObjectUtil.isNotNullEmpty(medias)){
                hasImage = true;
                userMessageBuilder.media(medias);
            }
        }

        UserMessage userMessage = userMessageBuilder.build();
        ChatClient.ChatClientRequestSpec chatClientRequest = null;
        if(hasImage){

            // 设置消息格式为图片
            userMessage.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

            Prompt chatPrompt = new Prompt(userMessage,
                    DashScopeChatOptions.builder()
                            .withModel(imageModelName)  // 使用视觉模型
                            .withMultiModel(true)             // 启用多模态
                            .withVlHighResolutionImages(true) // 启用高分辨率图片处理
                            .withTemperature(0.7)
                    .build());

            chatClientRequest = chatClient.prompt(chatPrompt);

        }else{
            chatClientRequest = chatClient.prompt(new Prompt(userMessage));
        }

        String promptConfig = param.getPromptConfig();
        if(ObjectUtil.isNotNullEmpty(promptConfig)){
            promptConfig = rpcConfigService.rpcGetValueByKey(promptConfig).getResultDataThrowError();
            if(ObjectUtil.isNotNullEmpty(promptConfig)){
                chatClientRequest.system(promptConfig);
            }
        }

        String chatStopLockKey = AI_CHAT_STOP_KEY + conversationId;
        StringRedisTemplate redisClient = RedisUtil.getRedisClient();
        ValueOperations<String, String> stringValueOperations = redisClient.opsForValue();

        StringBuilder aiChatOutputMessage = new StringBuilder();
        final String conversationIdFinal = conversationId.toString();
        return chatClientRequest
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationIdFinal))

                .stream()
                .chatResponse()

                // 请求前处理
                .doFirst(() -> stringValueOperations.setIfAbsent(chatStopLockKey, Boolean.TRUE.toString(), 60L, TimeUnit.SECONDS))

                // 异常时处理
                .doOnError(throwable -> redisClient.delete(chatStopLockKey))

                // 请求结束后处理
                .doOnComplete(() -> redisClient.delete(chatStopLockKey))

                // 是否打断对话流通道
                .takeWhile(chatResponse -> redisClient.hasKey(chatStopLockKey))

                // 打断对话流通道后执行
                .doOnCancel(() -> {
                    chatMemory.add(conversationIdFinal,new AssistantMessage(aiChatOutputMessage.toString()));
                    redisClient.delete(chatStopLockKey);
                })

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
    @SneakyThrows
    public AbstractBaseResult<ChatGenerateImageResult> generateImage(ChatGenerateImageParam param) {

        StringRedisTemplate redisClient = RedisUtil.getRedisClient();
        ValueOperations<String, String> stringValueOperations = redisClient.opsForValue();

        String conversationId = param.getConversationId().toString();

        Integer count = param.getCount();
        String chatStopLockKey = AI_CHAT_STOP_KEY + conversationId;
        String chatGenerateImageStopLockKey = AI_CHAT_GENERATE_IMAGE_STOP_KEY + conversationId;
        stringValueOperations.setIfAbsent(chatStopLockKey, Boolean.TRUE.toString(), 60L * count, TimeUnit.SECONDS);
        stringValueOperations.setIfAbsent(chatGenerateImageStopLockKey, Boolean.TRUE.toString(), 60L * count, TimeUnit.SECONDS);

        try{

            String message = param.getMessage();
            chatMemory.add(conversationId, new UserMessage(message));

            Integer height = param.getHeight();
            Integer width = param.getWidth();
            ImageOptions options = ImageOptionsBuilder.builder()
                    .height(ObjectUtil.isNotNullEmpty(height) ? height : 1024)
                    .width(ObjectUtil.isNotNullEmpty(width) ? width : 1024)
                    .N(ObjectUtil.isNotNullEmpty(count) ? count : 1)
                    .build();

            ImageResponse response = imageModel.call(new ImagePrompt(message, options));
            Set<String> images = response.getResults().stream().map(result -> result.getOutput().getUrl()).collect(Collectors.toSet());

            List<Media> medias = CollectionUtil.newArrayList();
            for(String image : images){
                URL url = URI.create(image).toURL();
                medias.add(new Media(MimeTypeUtils.IMAGE_PNG, new InputStreamResource(url.openStream())));
            }

            if(!redisClient.hasKey(chatGenerateImageStopLockKey)){
                return ResultUtil.success(new ChatGenerateImageResult(null,Set.of()));
            }

            String responseMessage = "图片生成完成 ...";
            if(ObjectUtil.isNotNullEmpty(medias)){
                chatMemory.add(conversationId, new AssistantMessage(responseMessage, CollectionUtil.newHashMap(), CollectionUtil.newArrayList(), medias));
            }

            return ResultUtil.success(new ChatGenerateImageResult(responseMessage, images));

        }finally {

            RedisUtil.getRedisClient().delete(
                    CollectionUtil.newArrayList(
                            chatStopLockKey,
                            chatGenerateImageStopLockKey
                    )
            );

        }

    }

    @Override
    public AbstractBaseResult<ChatListResult> list(String conversationId) {

        Boolean isLocked = RedisUtil.getRedisClient().hasKey(AI_CHAT_STOP_KEY + conversationId);

        List<Message> messages = chatMemoryRepository.findRecordByConversationId(conversationId);
        if(CollectionUtil.isEmpty(messages)){
            return ResultUtil.success(new ChatListResult(
                    isLocked,
                    CollectionUtil.newArrayList())
            );
        }

        return ResultUtil.success(new ChatListResult(isLocked,convertMessageList(messages)));

    }


    @Override
    public AbstractBaseResult<Void> stop(String conversationId) {
        RedisUtil.getRedisClient().delete(
                CollectionUtil.newArrayList(
                        AI_CHAT_STOP_KEY + conversationId,
                        AI_CHAT_GENERATE_IMAGE_STOP_KEY + conversationId
                )
        );
        return ResultUtil.success();
    }

    @Override
    public AbstractBaseResult<List<ChatItemResult>> getAfterMessages(String conversationId, Long createTime) {

        List<Message> messages = chatMemoryRepository.findRecordAfterTimestamp(conversationId, createTime);
        if(ObjectUtil.isNullEmpty(messages)){
            return ResultUtil.success(CollectionUtil.newArrayList());
        }

        return ResultUtil.success(convertMessageList(messages));

    }

    @Override
    public AbstractBaseResult<Void> removeMessage(String conversationId, String messageId) {
        chatMemoryRepository.deleteByMessageId(conversationId, messageId);
        return ResultUtil.success();
    }

}
