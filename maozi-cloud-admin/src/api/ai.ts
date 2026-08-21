import request from './request'
import {getAccessToken} from '@/utils/auth'
import {getGrayVersion} from '@/utils/gray'
import {getTempRequestUrl} from '@/utils/tempRequest'
import type {ApiResponse, ChatMessageItem, ConversationItem, PageResult} from '@/types/api'

/**
 * 会话列表查询参数
 */
export interface ConversationListParams {
  /** 当前页 */
  current?: number
  /** 每页数量 */
  size?: number
}

/**
 * AI 会话创建（以首条消息作为标题种子）
 * POST /ai/chat/record/create
 */
export function createConversation(message: string) {
  return request.post('/ai/chat/record/create', {
    data: message
  }) as unknown as Promise<ApiResponse<string>>
}

/**
 * AI 会话列表
 * POST /ai/chat/record/list
 */
export function getConversationList(params: ConversationListParams) {
  return request.post('/ai/chat/record/list', {
    current: params.current ?? 1,
    size: params.size ?? 100,
    data: {}
  }) as unknown as Promise<ApiResponse<PageResult<ConversationItem>>>
}

/**
 * AI 会话更新标题
 * POST /ai/chat/record/{conversationId}/updateTitle
 */
export function updateConversationTitle(
  conversationId: string | number,
  title: string
) {
  return request.post(
    `/ai/chat/record/${conversationId}/updateTitle`,
    { data: title }
  ) as unknown as Promise<ApiResponse<void>>
}

/**
 * AI 会话删除
 * POST /ai/chat/record/{conversationId}/remove
 */
export function removeConversation(conversationId: string | number) {
  return request.post(
    `/ai/chat/record/${conversationId}/remove`
  ) as unknown as Promise<ApiResponse<void>>
}

/**
 * AI 对话列表（当前会话的全部消息）
 * GET /ai/chat/{conversationId}/list
 */
export function getChatList(conversationId: string | number) {
  return request.get(
    `/ai/chat/${conversationId}/list`
  ) as unknown as Promise<ApiResponse<ChatMessageItem[]>>
}

/**
 * AI 对话停止
 * POST /ai/chat/{conversationId}/stop
 */
export function stopChat(conversationId: string | number) {
  return request.post(
    `/ai/chat/${conversationId}/stop`
  ) as unknown as Promise<ApiResponse<void>>
}

/**
 * AI 对话流事件（text/event-stream，元素为接口响应结果集）
 */
interface ChatStreamEvent {
  code: number
  data: {
    /** 0=完成对话 1=持续对话中 */
    type: number
    /** 增量消息内容（type=1 时存在） */
    message?: string
  }
  message?: string
}

/**
 * AI 对话回调
 */
export interface ChatStreamCallbacks {
  /** 收到增量内容 */
  onMessage: (chunk: string) => void
  /** 流正常结束（收到完成对话标记） */
  onFinish: () => void
  /** 流异常终止 */
  onError: (message: string) => void
}

/**
 * 组装 SSE 请求头：与 axios 实例保持一致（令牌、灰度标识）
 */
function buildStreamHeaders(): Record<string, string> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'text/event-stream'
  }
  const token = getAccessToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  const grayVersion = getGrayVersion()
  if (grayVersion) {
    headers['X-Version'] = grayVersion
  }
  return headers
}

/**
 * AI 对话（SSE 流式）
 * POST /ai/chat
 *
 * promptConfig 为可选的系统提示词配置名称（取配置项 name），未选择时不传
 * 返回中止函数：调用后断开流通道（服务端会保存已生成的部分内容）
 */
export async function chatStream(
  conversationId: string | number,
  message: string,
  callbacks: ChatStreamCallbacks,
  promptConfig?: string
): Promise<() => void> {
  const base = getTempRequestUrl() || import.meta.env.VITE_API_BASE_URL
  const controller = new AbortController()

  const abort = () => controller.abort()

  const consume = async () => {
    const response = await fetch(`${base}/ai/chat`, {
      method: 'POST',
      headers: buildStreamHeaders(),
      body: JSON.stringify({
        conversationId,
        message,
        promptConfig: promptConfig || undefined
      }),
      signal: controller.signal
    })

    if (!response.ok || !response.body) {
      callbacks.onError(`对话请求失败: ${response.status}`)
      return
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    const handleEvent = (payload: string) => {
      if (!payload) return
      let event: ChatStreamEvent
      try {
        event = JSON.parse(payload) as ChatStreamEvent
      } catch {
        return
      }
      if (event.code !== 200) {
        callbacks.onError(event.message || `业务码异常: ${event.code}`)
        return
      }
      if (event.data.type === 1) {
        callbacks.onMessage(event.data.message || '')
      } else if (event.data.type === 0) {
        callbacks.onFinish()
      }
    }

    try {
      for (;;) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })
        // SSE 事件以空行分隔，单个事件内可能存在多行 data
        const blocks = buffer.split('\n\n')
        buffer = blocks.pop() || ''
        blocks.forEach((block) => {
          const data = block
            .split('\n')
            .filter((line) => line.startsWith('data:'))
            .map((line) => line.slice(5).trimStart())
            .join('')
          handleEvent(data)
        })
      }
    } catch {
      // 主动中止（stop）不视为异常，由页面侧控制后续流程
    }
  }

  consume().catch((error) => {
    if (!controller.signal.aborted) {
      callbacks.onError(error?.message || '服务网络异常')
    }
  })

  return abort
}
