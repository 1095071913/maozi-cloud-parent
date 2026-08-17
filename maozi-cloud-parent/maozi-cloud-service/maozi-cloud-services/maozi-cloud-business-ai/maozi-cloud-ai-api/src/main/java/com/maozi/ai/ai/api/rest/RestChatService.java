package com.maozi.ai.ai.api.rest;

import com.maozi.ai.ai.dto.ChatParam;
import com.maozi.ai.ai.vo.ChatListResult;
import com.maozi.ai.ai.vo.ChatResult;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.service.annotation.Get;
import com.maozi.service.annotation.Post;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI交互会话管理服务 REST 接口
 * <p>
 * 提供AI交互会话的 RESTful API 接口定义，
 * 包括会话分页列表查询、会话新增、会话详情查询、
 * 会话更新、会话状态更新以及会话删除等操作。
 * 所有接口均需要相应的权限授权才能访问。
 * </p>
 */
@Tag(name = "AI对话模块")
public interface RestChatService {

	String PATH = "/chat";

	@Post(value = PATH, produces = MediaType.TEXT_EVENT_STREAM_VALUE, description = "AI对话")
    Flux<AbstractBaseResult<ChatResult>> chat(@RequestBody @Valid ChatParam param);

	@Get(value = PATH + "/{conversationId}/list", description = "AI对话列表")
	AbstractBaseResult<List<ChatListResult>> list(@PathVariable String conversationId);

	@Post(value = PATH + "/{conversationId}/stop", description = "AI对话停止")
	AbstractBaseResult<Void> stop(@PathVariable String conversationId);

}
