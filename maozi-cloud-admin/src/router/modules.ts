import type {Component} from 'vue'

/**
 * 业务页面组件注册表：key 为权限 mark，value 为组件懒加载函数。
 *
 * 后端 /system/permission/list 返回的菜单（type=1）没有 route 字段，
 * 因此前端按 mark 在此注册对应的页面组件；路由路径由 mark 推导
 * （markToPath：system:user -> /system/user）。
 *
 * 未在此注册的 mark 将统一落到 placeholder 占位页。
 * 新增业务页面时，在此追加即可，例如：
 *   'system:user': () => import('@/views/system/user/index.vue'),
 */
export const viewModules: Record<string, () => Promise<Component>> = {
  // 账号管理
  'system:user:list': () => import('@/views/system/user/index.vue'),
  // 角色管理
  'system:role:list': () => import('@/views/system/role/index.vue'),
  // 权限管理
  'system:permission:list': () => import('@/views/system/permission/index.vue'),
  // 客户端管理
  'system:client:list': () => import('@/views/system/client/index.vue'),
  // 配置管理
  'system:config:list': () => import('@/views/system/config/index.vue'),
  // 中间件管理
  'system:middleware:list': () => import('@/views/system/middleware/index.vue')
}
