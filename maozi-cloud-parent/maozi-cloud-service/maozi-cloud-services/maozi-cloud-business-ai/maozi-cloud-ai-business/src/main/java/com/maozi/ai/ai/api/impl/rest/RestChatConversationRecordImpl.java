package com.maozi.ai.ai.api.impl.rest;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.maozi.ai.ai.api.impl.ChatConversationRecordServiceImpl;
import com.maozi.ai.ai.api.rest.RestChatConversationRecord;
import com.maozi.ai.ai.domain.ChatConversationRecordDo;
import com.maozi.ai.ai.dto.ChatConversationRecordListParam;
import com.maozi.ai.ai.vo.ChatConversationRecordListResult;
import com.maozi.base.param.PageParam;
import com.maozi.base.param.RequestParam;
import com.maozi.base.result.PageResult;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.context.ApplicationLinkContext;
import com.maozi.common.dto.CurrentUserInfo;
import com.maozi.common.result.AbstractBaseResult;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.service.api.annotation.RestService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;

/**
 * @author pengjinlong
 * @since 2026/8/16 21:49
 */
@RestService
@RequiredArgsConstructor
public class RestChatConversationRecordImpl extends ChatConversationRecordServiceImpl implements RestChatConversationRecord {

    private final ChatMemory chatMemory;

    @Override
    public AbstractBaseResult<PageResult<ChatConversationRecordListResult>> restList(PageParam<ChatConversationRecordListParam> pageParam) {

        Long userId = ApplicationLinkContext.getCurrentUserInfo(CurrentUserInfo::getUserId);
        MPJLambdaWrapper<ChatConversationRecordDo> wrapper = buildQueryWrapper(pageParam, ChatConversationRecordListResult.class);
        wrapper.eq(ChatConversationRecordDo::getUserId,userId);

        Page<ChatConversationRecordListResult> page = selectJoinListPage(convertPage(pageParam),ChatConversationRecordListResult.class,wrapper);

        PageResult<ChatConversationRecordListResult> pageResult = convertPageResult(page);

        setRelationData(pageResult, ChatConversationRecordListResult.class);

        return ResultUtil.success(pageResult);
    }

    @Override
    public AbstractBaseResult<Long> restCreate(RequestParam<String> param) {

        String title = param.getData();
        if(ObjectUtil.isNullEmpty(title)){
            throw new BusinessResultException("消息不能为空");
        }

        ChatConversationRecordDo domain = new ChatConversationRecordDo();
        domain.setTitle(title.length() > 20 ? StrUtil.subPre(title,20) + "..." : title);
        domain.setUserId(ApplicationLinkContext.getCurrentUserInfo(CurrentUserInfo::getUserId));

        save(domain);

        return ResultUtil.success(domain.getId());

    }

    @Override
    public AbstractBaseResult<Void> restUpdateTitle(Long conversationId, RequestParam<String> param) {

        String title = param.getData();
        if(ObjectUtil.isNullEmpty(title)){
            throw new BusinessResultException("标题不能为空");
        }

        if(title.length() > 20){
            throw new BusinessResultException("标题不能大于20字符");
        }

        ChatConversationRecordDo domain = new ChatConversationRecordDo();
        domain.setId(conversationId);
        domain.setTitle(title);
        updateById(domain);

        return ResultUtil.success();

    }

    @Override
    public AbstractBaseResult<Void> restRemove(String conversationId) {

        removeById(conversationId);
        chatMemory.clear(conversationId);

        return ResultUtil.success();

    }

}
