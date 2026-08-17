<script setup lang="ts">
import { computed } from 'vue'
import { Marked } from 'marked'
import DOMPurify from 'dompurify'

const props = defineProps<{
  /** Markdown 原文（支持流式追加过程中的不完整内容） */
  content: string
}>()

/** 独立实例，避免污染全局配置：GFM 语法 + 单行换行转为 <br>（贴合对话场景） */
const parser = new Marked({ gfm: true, breaks: true })

/** 渲染结果经 DOMPurify 消毒，防止 AI 输出中混入恶意 HTML */
const html = computed(() => {
  const raw = parser.parse(props.content || '', { async: false }) as string
  return DOMPurify.sanitize(raw)
})
</script>

<template>
  <!-- 内容已消毒，可安全渲染 -->
  <div class="md-content" v-html="html"></div>
</template>

<style scoped lang="scss">
$md-border: #eceef3;
$md-text-primary: #303133;
$md-text-secondary: #606266;

.md-content {
  min-width: 0;
  font-size: 14px;
  line-height: 1.7;
  color: $md-text-primary;
  word-break: break-word;

  // v-html 渲染的内容不带 scoped 属性，必须用 :deep() 穿透才能命中
  > :deep(:first-child) {
    margin-top: 0;
  }

  > :deep(:last-child) {
    margin-bottom: 0;
  }

  :deep(p) {
    margin: 0 0 8px;
  }

  :deep(h1),
  :deep(h2),
  :deep(h3),
  :deep(h4),
  :deep(h5),
  :deep(h6) {
    margin: 14px 0 8px;
    color: $md-text-primary;
    font-weight: 600;
  }

  :deep(h1) {
    font-size: 18px;
  }

  :deep(h2) {
    font-size: 16.5px;
  }

  :deep(h3) {
    font-size: 15px;
  }

  :deep(h4),
  :deep(h5),
  :deep(h6) {
    font-size: 14px;
  }

  :deep(ul),
  :deep(ol) {
    margin: 0 0 8px;
    padding-left: 20px;
  }

  :deep(li) {
    margin: 2px 0;
  }

  :deep(li > ul),
  :deep(li > ol) {
    margin: 2px 0;
  }

  :deep(code) {
    padding: 2px 6px;
    border-radius: 4px;
    background: rgba(99, 102, 241, 0.08);
    color: #4f46e5;
    font-size: 13px;
    font-family: 'JetBrains Mono', Consolas, Menlo, Monaco, monospace;
  }

  :deep(pre) {
    margin: 0 0 10px;
    padding: 12px 14px;
    border-radius: 8px;
    background: #1e2030;
    overflow-x: auto;
    line-height: 1.6;

    code {
      padding: 0;
      background: transparent;
      color: #e6e7f0;
    }
  }

  :deep(blockquote) {
    margin: 0 0 8px;
    padding: 6px 12px;
    border-left: 3px solid #a5b4fc;
    border-radius: 0 6px 6px 0;
    background: #f5f6ff;
    color: $md-text-secondary;

    > :last-child {
      margin-bottom: 0;
    }
  }

  :deep(table) {
    width: 100%;
    margin: 0 0 8px;
    border-collapse: collapse;
    font-size: 13px;

    th,
    td {
      padding: 6px 10px;
      border: 1px solid $md-border;
      text-align: left;
    }

    th {
      background: #f5f6fa;
      color: $md-text-primary;
      font-weight: 600;
    }
  }

  :deep(a) {
    color: #6366f1;
  }

  :deep(strong) {
    font-weight: 600;
  }

  :deep(hr) {
    margin: 12px 0;
    border: none;
    border-top: 1px solid $md-border;
  }

  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
  }
}
</style>
