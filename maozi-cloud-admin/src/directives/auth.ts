import type {Directive} from 'vue'
import {useUserStore} from '@/store/modules/user'

/**
 * 按钮级权限指令。
 * 用法：
 *   v-auth="'system:user:save'"          // 单个 mark
 *   v-auth="['system:user:save', '...']"  // 任一 mark 命中即显示
 *
 * 判断依据：用户自身 permissions 是否包含对应 mark。
 */
function check(el: HTMLElement, value: string | string[]) {
  const marks = Array.isArray(value) ? value : [value]
  if (marks.length === 0) return
  const userStore = useUserStore()
  const ok = marks.some((m) => userStore.permissions.includes(m))
  if (!ok) {
    el.style.display = 'none'
  } else {
    if (el.style.display === 'none') el.style.display = ''
  }
}

export const auth: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    check(el, binding.value)
  },
  updated(el, binding) {
    check(el, binding.value)
  }
}
