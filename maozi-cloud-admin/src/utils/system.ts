/** 环境标识展示信息 */
export interface EnvironmentInfo {
  /** 展示标签 */
  label: string
  /** 标签颜色类型 */
  type: 'danger' | 'warning' | 'primary' | 'info' | 'success'
}

/**
 * 按环境名映射标签与颜色（全局显眼展示）
 * prod=生产 pre/staging/gray=预发 test=测试 dev=开发 local=本地
 */
export function environmentInfo(environment: string): EnvironmentInfo | null {
  const env = environment.toLowerCase()
  if (!env) return null
  if (env.includes('prod')) return { label: '生产环境', type: 'danger' }
  if (env.includes('pre') || env.includes('staging') || env.includes('gray'))
    return { label: '预发环境', type: 'warning' }
  if (env.includes('test')) return { label: '测试环境', type: 'warning' }
  if (env.includes('dev')) return { label: '开发环境', type: 'primary' }
  if (env.includes('local')) return { label: '本地环境', type: 'success' }
  return { label: env, type: 'info' }
}
