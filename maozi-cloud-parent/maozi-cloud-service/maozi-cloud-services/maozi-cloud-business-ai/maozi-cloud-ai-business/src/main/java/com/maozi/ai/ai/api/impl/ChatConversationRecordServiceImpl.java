package com.maozi.ai.ai.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.maozi.ai.ai.api.ChatConversationRecordService;
import com.maozi.ai.ai.domain.ChatConversationRecordDo;
import com.maozi.ai.ai.mapper.ChatConversationRecordMapper;
import com.maozi.service.api.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * AI交互会话服务实现类
 * <p>实现AI交互会话相关的业务逻辑，包括会话的保存/更新、
 * 详情查询和分页列表查询等功能。</p>
 */
@Service
public class ChatConversationRecordServiceImpl extends BaseServiceImpl<ChatConversationRecordMapper, ChatConversationRecordDo,Void> implements ChatConversationRecordService {

	/** 资源名称，用于异常提示信息 */
	private static final String RESOURCE_NAME = "AI聊天会话记录";

	/**
	 * 获取资源名称
	 *
	 * @return 资源名称字符串
	 */
	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}


	@Override
	public void has(Long conversationId) {
		LambdaQueryWrapper<ChatConversationRecordDo> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ChatConversationRecordDo::getId, conversationId);
		checkHas(wrapper);
	}

}
