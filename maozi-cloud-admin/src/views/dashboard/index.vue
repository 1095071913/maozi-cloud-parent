<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotRound as ChatIcon,
  Delete,
  Edit,
  MagicStick,
  Plus,
  Promotion,
  VideoPause
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import {
  chatStream,
  createConversation,
  getChatList,
  getConversationList,
  removeConversation,
  stopChat,
  updateConversationTitle
} from '@/api/ai'
import { ChatMessageType, type ChatMessageItem, type ConversationItem } from '@/types/api'

const userStore = useUserStore()

/** 空状态快捷提问 */
const suggestions = [
  '帮我写一封周报',
  '用通俗的语言解释一下微服务',
  '写一个 SQL 优化的小技巧',
  '给我推荐几本技术书籍'
]

function handleSuggestion(text: string) {
  input.value = text
}

// ============ 会话列表 ============
const conversations = ref<ConversationItem[]>([])
const total = ref(0)
const current = ref(1)
const conversationLoading = ref(false)

const hasMore = computed(() => conversations.value.length < total.value)

async function loadConversations(page = 1) {
  conversationLoading.value = true
  try {
    const res = await getConversationList({ current: page, size: 100 })
    const pageData = res.data
    total.value = Number(pageData.total) || 0
    current.value = Number(pageData.current) || page
    const items = pageData.data || []
    conversations.value = page === 1 ? items : [...conversations.value, ...items]
  } finally {
    conversationLoading.value = false
  }
}

function handleLoadMore() {
  loadConversations(current.value + 1)
}

/** 新建对话：清空输入区，首次发送时再真正创建会话 */
function handleCreate() {
  stopStreamSilently()
  activeId.value = ''
  messages.value = []
}

// ============ 会话选中 / 更新标题 / 删除 ============
const activeId = ref<string | number>('')

onMounted(async () => {
  await loadConversations(1)
  // 默认选中最近一个会话，直接进入可对话状态
  if (conversations.value.length) {
    activeId.value = conversations.value[0].id
    loadMessages()
  }
})

async function handleSelect(row: ConversationItem) {
  if (row.id === activeId.value) return
  stopStreamSilently()
  activeId.value = row.id
  await loadMessages()
}

async function handleUpdateTitle(row: ConversationItem) {
  const { value } = await ElMessageBox.prompt('请输入会话标题', '更新标题', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: row.title
  }).catch(() => ({ value: '' }) as { value: string })
  const title = (value || '').trim()
  if (!title) return
  await updateConversationTitle(row.id, title)
  row.title = title
  ElMessage.success('更新成功')
}

async function handleRemove(row: ConversationItem) {
  await ElMessageBox.confirm(`确定删除会话「${row.title}」吗？`, '提示', {
    type: 'warning'
  })
  await removeConversation(row.id)
  ElMessage.success('删除成功')
  if (row.id === activeId.value) {
    activeId.value = ''
    messages.value = []
  }
  await loadConversations(1)
}

// ============ 对话消息 ============
interface ChatBubble extends ChatMessageItem {
  /** 流式输出中 */
  streaming?: boolean
}

const messages = ref<ChatBubble[]>([])
const messageLoading = ref(false)
const scrollRef = ref<HTMLElement>()

const activeTitle = computed(
  () => conversations.value.find((c) => c.id === activeId.value)?.title || 'AI 对话'
)

async function loadMessages() {
  if (!activeId.value) return
  messageLoading.value = true
  try {
    const res = await getChatList(activeId.value)
    messages.value = (res.data || []).map((m) => ({ ...m }))
    scrollToBottom()
  } finally {
    messageLoading.value = false
  }
}

function scrollToBottom() {
  nextTick(() => {
    const el = scrollRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

// ============ 发送 / 停止 ============
const input = ref('')
const sending = ref(false)
let abortStream: (() => void) | null = null
/** 正在生成中的会话 ID（用于切换/新建时通知服务端停止） */
let streamingConversationId: string | number = ''

// ============ 打字机效果 ============
/** 打字机帧间隔（毫秒），逐字上屏节奏 */
const TYPE_INTERVAL = 20
let typingTimer: ReturnType<typeof setInterval> | null = null
/** 已接收但尚未上屏的内容（打字机缓冲） */
let pendingText = ''
/** 流是否已结束（结束后缓冲打完即可收尾） */
let streamFinished = false
/** 打字机正在输出的 AI 气泡 */
let typingTarget: ChatBubble | null = null

/** 启动打字机：流内容先入缓冲，由定时器逐字上屏 */
function startTyping(target: ChatBubble) {
  stopTypingTimer()
  typingTarget = target
  pendingText = ''
  streamFinished = false
  typingTimer = setInterval(() => {
    if (!typingTarget) return
    if (!pendingText.length) {
      // 缓冲已空：流结束后收尾，否则继续等待后续内容
      if (streamFinished) finishTyping()
      return
    }
    // 按积压量自适应每帧字数，保证上屏速度追得上流速度
    const step = Math.max(1, Math.ceil(pendingText.length / 30))
    typingTarget.message += pendingText.slice(0, step)
    pendingText = pendingText.slice(step)
    scrollToBottom()
  }, TYPE_INTERVAL)
}

function stopTypingTimer() {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
}

/** 打字机收尾：结束本条消息输出 */
function finishTyping() {
  stopTypingTimer()
  if (typingTarget) {
    typingTarget.streaming = false
    typingTarget = null
  }
  pendingText = ''
  sending.value = false
  abortStream = null
  streamingConversationId = ''
}

/** 立即输出缓冲中的全部内容并收尾（停止/异常/切换会话时） */
function flushTyping() {
  if (!typingTarget) return
  typingTarget.message += pendingText
  finishTyping()
}

/** 中断当前流（切换/新建会话时静默处理，不打扰用户） */
function stopStreamSilently() {
  if (abortStream) {
    const id = streamingConversationId
    stopChat(id).catch(() => {})
    abortStream()
    abortStream = null
    streamingConversationId = ''
  }
  flushTyping()
  messages.value.forEach((m) => (m.streaming = false))
  sending.value = false
}

async function handleSend() {
  const message = input.value.trim()
  if (!message || sending.value) return

  let conversationId = activeId.value
  try {
    sending.value = true
    // 无选中会话：先以首条消息创建会话（后端以该消息生成标题）
    if (!conversationId) {
      const created = await createConversation(message)
      conversationId = created.data
      activeId.value = conversationId
      await loadConversations(1)
    }

    input.value = ''
    messages.value.push({ type: ChatMessageType.USER, message })
    // 必须为 reactive 代理对象：打字机定时器逐字修改 message 时才能触发视图更新
    const aiMessage: ChatBubble = reactive({
      type: ChatMessageType.AI,
      message: '',
      streaming: true
    })
    messages.value.push(aiMessage)
    scrollToBottom()
    startTyping(aiMessage)

    abortStream = await chatStream(conversationId, message, {
      onMessage: (chunk) => {
        // 流内容先进缓冲，由打字机逐字上屏
        pendingText += chunk
      },
      onFinish: () => {
        // 流结束：待缓冲内容逐字打完后自动收尾
        streamFinished = true
      },
      onError: (msg) => {
        // 异常：已收到的内容立即完整展示
        flushTyping()
        ElMessage.error(msg)
      }
    })
    streamingConversationId = conversationId
  } catch {
    flushTyping()
  }
}

async function handleStop() {
  if (!activeId.value) return
  // 服务端停止生成并落库已生成的部分内容，随后本地断开流通道
  await stopChat(activeId.value).catch(() => {})
  stopStreamSilently()
  // 服务端保存已生成内容存在轻微异步延迟，稍候再拉取最终消息
  await new Promise((resolve) => setTimeout(resolve, 300))
  await loadMessages()
}

onBeforeUnmount(() => {
  if (abortStream) {
    stopChat(streamingConversationId).catch(() => {})
    abortStream()
  }
  stopTypingTimer()
})

// ============ 输入法组合输入状态 ============
/** 是否处于输入法组合输入中（由 compositionstart/end 维护） */
let composing = false
/** 组合输入刚结束的时间戳（Safari 的选词回车 keydown 晚于 compositionend 触发，用时间差兜底） */
let compositionEndTime = 0

function handleCompositionStart() {
  composing = true
}

function handleCompositionEnd() {
  composing = false
  compositionEndTime = performance.now()
}

function handleInputKeydown(event: Event) {
  const e = event as KeyboardEvent
  if (e.key !== 'Enter' || e.shiftKey) return
  // 输入法选词/上屏的回车不发送：Chrome 等可由 isComposing 识别；
  // Safari 的 compositionend 先于 keydown（isComposing 已为 false），用自维护状态与刚结束时间兜底
  if (
    e.isComposing ||
    e.keyCode === 229 ||
    composing ||
    performance.now() - compositionEndTime < 30
  ) {
    return
  }
  e.preventDefault()
  handleSend()
}
</script>

<template>
  <div class="chat-page">
    <!-- ============ 左侧会话面板 ============ -->
    <aside class="conversation-panel">
      <div class="panel-header">
        <div class="brand">
          <span class="brand-logo">
            <el-icon :size="16"><MagicStick /></el-icon>
          </span>
          <span class="brand-name">AI 对话</span>
        </div>
        <button class="create-btn" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          <span>新建对话</span>
        </button>
      </div>

      <div v-loading="conversationLoading" class="conversation-list">
        <div
          v-for="row in conversations"
          :key="row.id"
          class="conversation-item"
          :class="{ active: row.id === activeId }"
          @click="handleSelect(row)"
        >
          <el-icon class="item-icon"><ChatIcon /></el-icon>
          <span class="item-title" :title="row.title">{{ row.title }}</span>
          <span class="item-actions" @click.stop>
            <button class="action-btn" title="更新标题" @click="handleUpdateTitle(row)">
              <el-icon><Edit /></el-icon>
            </button>
            <button class="action-btn" title="删除会话" @click="handleRemove(row)">
              <el-icon><Delete /></el-icon>
            </button>
          </span>
        </div>
        <div v-if="hasMore" class="load-more" @click="handleLoadMore">加载更多</div>
        <div v-if="!conversationLoading && !conversations.length" class="list-empty">
          <el-icon :size="22"><ChatIcon /></el-icon>
          <p>暂无会话</p>
        </div>
      </div>

      <div class="panel-footer">共 {{ total }} 个会话</div>
    </aside>

    <!-- ============ 右侧对话面板 ============ -->
    <section class="chat-panel">
      <header class="chat-header">
        <div class="header-info">
          <h3 class="header-title">{{ activeTitle }}</h3>
          <div class="header-sub">
            <span class="status-dot" />
            AI 助手在线
          </div>
        </div>
      </header>

      <div ref="scrollRef" v-loading="messageLoading" class="message-list">
        <!-- 空状态欢迎页 -->
        <div v-if="!messages.length" class="welcome">
          <div class="welcome-icon">
            <el-icon :size="30"><MagicStick /></el-icon>
          </div>
          <h2 class="welcome-title">欢迎回来{{ userStore.name }}，有什么可以帮助您 !</h2>
          <p class="welcome-desc">输入问题开始对话，或从下面的话题开始</p>
          <div class="welcome-suggestions">
            <button v-for="s in suggestions" :key="s" @click="handleSuggestion(s)">
              {{ s }}
            </button>
          </div>
        </div>

        <!-- 消息气泡 -->
        <div
          v-for="(item, index) in messages"
          :key="index"
          class="message-row"
          :class="item.type === ChatMessageType.USER ? 'self' : 'ai'"
        >
          <div class="avatar">
            <template v-if="item.type === ChatMessageType.USER">
              <img v-if="userStore.icon" class="avatar-img" :src="userStore.icon" alt="" />
              <template v-else>{{ userStore.name.slice(0, 1) }}</template>
            </template>
            <el-icon v-else :size="17"><MagicStick /></el-icon>
          </div>

          <div class="bubble" :class="{ markdown: item.type === ChatMessageType.AI }">
            <!-- 等待首个内容时显示打字指示 -->
            <span v-if="item.streaming && !item.message" class="typing">
              <i /><i /><i />
            </span>
            <template v-else>
              <!-- AI 输出按 Markdown 渲染，用户消息保持纯文本 -->
              <ChatMarkdown v-if="item.type === ChatMessageType.AI" :content="item.message" />
              <span v-else class="plain">{{ item.message }}</span>
              <span v-if="item.streaming" class="cursor">▍</span>
            </template>
          </div>
        </div>
      </div>

      <!-- ============ 输入区 ============ -->
      <footer class="input-area">
        <div class="input-card">
          <el-input
            v-model="input"
            type="textarea"
            :rows="3"
            resize="none"
            placeholder="请输入您的问题…"
            @keydown="handleInputKeydown"
            @compositionstart="handleCompositionStart"
            @compositionend="handleCompositionEnd"
          />
          <div class="input-toolbar">
            <span class="input-hint">Enter 发送 · Shift + Enter 换行</span>
            <el-button
              v-if="sending"
              class="stop-btn"
              type="danger"
              plain
              :icon="VideoPause"
              @click="handleStop"
            >
              停止
            </el-button>
            <el-button
              v-else
              class="send-btn"
              type="primary"
              :icon="Promotion"
              :disabled="!input.trim()"
              @click="handleSend"
            >
              发送
            </el-button>
          </div>
        </div>
      </footer>
    </section>
  </div>
</template>

<style scoped lang="scss">
/** 品牌渐变主色 */
$accent: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
$accent-hover: linear-gradient(135deg, #5558e8 0%, #7c4df2 100%);
$border-color: #eceef3;
$text-primary: #303133;
$text-secondary: #606266;

.chat-page {
  display: flex;
  gap: 16px;
  height: 100%;
  min-height: 0;
  background: linear-gradient(180deg, #f8f9fc 0%, #f1f3f9 100%);
}

// ============ 会话面板 ============
.conversation-panel {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  width: 264px;
  background: #fff;
  border: 1px solid $border-color;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(31, 35, 41, 0.04);
  overflow: hidden;
}

.panel-header {
  padding: 16px 14px 12px;
  border-bottom: 1px solid #f2f3f7;
}

.brand {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 12px;
}

.brand-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: $accent;
  color: #fff;
  box-shadow: 0 3px 8px rgba(99, 102, 241, 0.35);
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  color: $text-primary;
  letter-spacing: 0.5px;
}

.create-btn {
  display: flex;
  gap: 6px;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 38px;
  border: none;
  border-radius: 10px;
  background: $accent;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: $accent-hover;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
}

.conversation-list {
  flex: 1;
  overflow: auto;
  padding: 8px;
}

.conversation-item {
  position: relative;
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 4px;
  padding: 10px 10px 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f6fa;

    .item-actions {
      visibility: visible;
    }
  }

  &.active {
    background: linear-gradient(135deg, #eef2ff 0%, #f3efff 100%);

    .item-icon {
      color: #6366f1;
    }

    .item-title {
      color: #4f46e5;
      font-weight: 600;
    }

    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      width: 3px;
      height: 18px;
      border-radius: 2px;
      background: $accent;
      transform: translateY(-50%);
    }
  }
}

.item-icon {
  flex-shrink: 0;
  color: #a8abb2;
  transition: color 0.2s;
}

.item-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13.5px;
  color: $text-primary;
}

.item-actions {
  display: flex;
  gap: 4px;
  visibility: hidden;
  flex-shrink: 0;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  padding: 0;
  border: none;
  border-radius: 6px;
  background: #fff;
  color: #909399;
  font-size: 13px;
  cursor: pointer;
  box-shadow: 0 1px 4px rgba(31, 35, 41, 0.12);

  &:hover {
    color: #6366f1;
  }
}

.load-more {
  padding: 8px 0;
  color: #6366f1;
  font-size: 13px;
  text-align: center;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
}

.list-empty {
  padding: 40px 0;
  color: #c0c4cc;
  font-size: 13px;
  text-align: center;

  p {
    margin: 8px 0 0;
  }
}

.panel-footer {
  flex-shrink: 0;
  padding: 10px;
  border-top: 1px solid #f2f3f7;
  color: #a8abb2;
  font-size: 12px;
  text-align: center;
}

// ============ 对话面板 ============
.chat-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  background: #fff;
  border: 1px solid $border-color;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(31, 35, 41, 0.04);
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  padding: 12px 20px;
  border-bottom: 1px solid #f2f3f7;
}

.header-title {
  margin: 0;
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 15px;
  font-weight: 600;
  color: $text-primary;
}

.header-sub {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-left: auto;
  color: #909399;
  font-size: 12px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #67c23a;
  box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.18);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  50% {
    box-shadow: 0 0 0 5px rgba(103, 194, 58, 0.08);
  }
}

// ============ 消息区 ============
.message-list {
  flex: 1;
  overflow: auto;
  padding: 24px;
  background: linear-gradient(180deg, #fbfcfe 0%, #f7f8fc 100%);

  &::-webkit-scrollbar,
  .conversation-list::-webkit-scrollbar {
    width: 5px;
  }

  &::-webkit-scrollbar-thumb,
  .conversation-list::-webkit-scrollbar-thumb {
    border-radius: 3px;
    background: #dcdfe6;

    &:hover {
      background: #c0c4cc;
    }
  }
}

// ---- 空状态欢迎 ----
.welcome {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  text-align: center;
}

.welcome-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 20px;
  border-radius: 20px;
  background: $accent;
  color: #fff;
  box-shadow: 0 10px 24px rgba(99, 102, 241, 0.35);
}

.welcome-title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  background: $accent;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-desc {
  margin: 10px 0 24px;
  color: #909399;
  font-size: 14px;
}

.welcome-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  max-width: 520px;

  button {
    padding: 8px 16px;
    border: 1px solid $border-color;
    border-radius: 999px;
    background: #fff;
    color: $text-secondary;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #6366f1;
      color: #6366f1;
      background: #f5f6ff;
      box-shadow: 0 2px 8px rgba(99, 102, 241, 0.15);
    }
  }
}

// ---- 消息气泡 ----
.message-row {
  display: flex;
  gap: 10px;
  margin-bottom: 22px;
  animation: rise 0.25s ease-out;

  .avatar {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    width: 34px;
    height: 34px;
    border-radius: 10px;
    color: #fff;
    font-size: 14px;
    font-weight: 600;
    overflow: hidden;
    white-space: nowrap;
  }

  .avatar-img {
    width: 100%;
    height: 100%;
    border-radius: inherit;
    object-fit: cover;
  }

  .bubble {
    max-width: 72%;
    padding: 10px 14px;
    font-size: 14px;
    line-height: 1.7;
    word-break: break-word;

    /** Markdown 气泡由内容组件自行排版，禁用 pre-wrap 避免多余空行 */
    &.markdown {
      white-space: normal;
      min-width: 0;
    }
  }

  &.ai {
    .avatar {
      background: $accent;
      box-shadow: 0 3px 8px rgba(139, 92, 246, 0.3);
    }

    .bubble {
      border: 1px solid $border-color;
      border-radius: 2px 12px 12px 12px;
      background: #fff;
      color: $text-primary;
      box-shadow: 0 1px 6px rgba(31, 35, 41, 0.05);
    }
  }

  &.self {
    flex-direction: row-reverse;

    .avatar {
      background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
      box-shadow: 0 3px 8px rgba(59, 130, 246, 0.3);
    }

    .bubble {
      border-radius: 12px 2px 12px 12px;
      background: linear-gradient(135deg, #4f7cf0 0%, #6366f1 100%);
      color: #fff;
      box-shadow: 0 3px 10px rgba(79, 124, 240, 0.3);

      /** 用户消息保持纯文本按原始换行展示 */
      .plain {
        white-space: pre-wrap;
      }
    }
  }
}

@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
}

/** 打字指示（等待首个内容） */
.typing {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  padding: 4px 0;

  i {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #a5b4fc;
    animation: bounce 1.2s infinite ease-in-out;

    &:nth-child(2) {
      animation-delay: 0.15s;
    }

    &:nth-child(3) {
      animation-delay: 0.3s;
    }
  }
}

@keyframes bounce {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.5;
  }

  30% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

.cursor {
  margin-left: 1px;
  color: #6366f1;
  animation: blink 1s infinite;
}

@keyframes blink {
  50% {
    opacity: 0;
  }
}

// ============ 输入区 ============
.input-area {
  flex-shrink: 0;
  padding: 14px 18px 16px;
  border-top: 1px solid #f2f3f7;
  background: #fff;
}

.input-card {
  border: 1px solid $border-color;
  border-radius: 12px;
  background: #fafbfd;
  transition: border-color 0.2s, box-shadow 0.2s;

  &:focus-within {
    border-color: #a5b4fc;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.12);
  }

  :deep(.el-textarea__inner) {
    border: none;
    box-shadow: none !important;
    background: transparent;
    padding: 12px 14px 4px;
    font-size: 14px;
    line-height: 1.6;
  }
}

.input-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px 8px 14px;
}

.input-hint {
  color: #c0c4cc;
  font-size: 12px;
}

.chat-page .el-button.send-btn {
  border: none;
  border-radius: 10px;
  background: $accent;
  box-shadow: 0 3px 10px rgba(99, 102, 241, 0.35);
  font-weight: 500;

  &:hover,
  &:focus {
    background: $accent-hover;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.45);
  }

  &.is-disabled {
    background: #c8c9cc;
    box-shadow: none;
  }
}

.chat-page .el-button.stop-btn {
  border-radius: 10px;
}
</style>
