package com.maozi.ai.ai.api.rest;

import com.maozi.ai.ai.dto.ChatConversationRecordListParam;
import com.maozi.ai.ai.vo.ChatConversationRecordListResult;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.RequestParam;
import com.maozi.base.result.PageResult;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.service.annotation.Post;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * AI会话管理服务 REST 接口
 * <p>
 * 提供AI对话会话的 RESTful API 接口定义，
 * 包括当前用户的会话分页列表查询、会话创建、
 * 会话标题更新以及会话删除等操作。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/8/16 21:35
 */
@Tag(name = "AI会话模块")
public interface RestChatConversationRecord {

    String PATH = "/chat/record";

    @Post(value = PATH + "/list", description = "AI会话列表")
    AbstractBaseResult<PageResult<ChatConversationRecordListResult>> restList(@RequestBody PageParam<ChatConversationRecordListParam> pageParam);

    @Post(value = PATH + "/create", description = "AI会话创建")
    AbstractBaseResult<Long> restCreate(@RequestBody RequestParam<String> param);

    @Post(value = PATH + "/{conversationId}/updateTitle", description = "AI会话更新标题")
    AbstractBaseResult<Void> restUpdateTitle(@PathVariable Long conversationId, @RequestBody RequestParam<String> param);

    @Post(value = PATH + "/{conversationId}/remove", description = "AI会话删除")
    AbstractBaseResult<Void> restRemove(@PathVariable String conversationId);

}
