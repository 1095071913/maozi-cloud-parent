const ACCESS_TOKEN_KEY = 'maozi_access_token'
const REFRESH_TOKEN_KEY = 'maozi_refresh_token'

/**
 * 令牌存取（localStorage + 内存副本 + Cookie 备份）：
 *
 * 内嵌的同源页面（如经代理加载的 Nacos 控制台）登出时会执行 localStorage.clear()，
 * 把本站令牌一并清空。仅靠内存副本时，页面一旦刷新/重开（内存副本从已清空的
 * localStorage 重新初始化）登录态即丢失，因此额外镜像一份到会话 Cookie：
 * - localStorage 被整体清空后，刷新/重开页面仍可从 Cookie 无感恢复；
 * - Cookie 不会被 localStorage.clear() 波及；
 * - 令牌本就存于同源 localStorage，内嵌页原本即可读，Cookie 镜像不增加暴露面。
 *
 * 本应用自身登出走 clearTokens()（逐键 remove + 清 Cookie），不受恢复逻辑影响。
 */
const ACCESS_TOKEN_COOKIE = 'maozi_access_token_bak'
const REFRESH_TOKEN_COOKIE = 'maozi_refresh_token_bak'

function readCookie(name: string): string {
  const match = document.cookie.match(new RegExp(`(?:^|;\\s*)${name}=([^;]*)`))
  return match ? decodeURIComponent(match[1]) : ''
}

/** 会话级 Cookie（关闭浏览器失效），仅作 localStorage 被外清后的恢复备份 */
function writeCookie(name: string, value: string): void {
  document.cookie = value
    ? `${name}=${encodeURIComponent(value)}; path=/; SameSite=Lax`
    : `${name}=; path=/; Max-Age=0`
}

/** 初始化：优先 localStorage；其为空但 Cookie 备份存在（被外清后刷新的场景）则恢复 */
function initValue(lsKey: string, cookieKey: string): string {
  const value = localStorage.getItem(lsKey) || ''
  if (value) {
    writeCookie(cookieKey, value)
    return value
  }
  const backup = readCookie(cookieKey)
  if (backup) {
    localStorage.setItem(lsKey, backup)
  }
  return backup
}

let accessTokenCache = initValue(ACCESS_TOKEN_KEY, ACCESS_TOKEN_COOKIE)
let refreshTokenCache = initValue(REFRESH_TOKEN_KEY, REFRESH_TOKEN_COOKIE)

/** 将内存副本持久化到 localStorage 与 Cookie 备份 */
function persistTokens(): void {
  if (accessTokenCache) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessTokenCache)
  } else {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
  }
  if (refreshTokenCache) {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshTokenCache)
  } else {
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  }
  writeCookie(ACCESS_TOKEN_COOKIE, accessTokenCache)
  writeCookie(REFRESH_TOKEN_COOKIE, refreshTokenCache)
}

// 其他同源文档整体清空 localStorage（storage 事件 key 为 null，如内嵌 Nacos 登出）时立即回写。
// 本应用自身登出为逐键 remove（事件 key 为具体键名），不会触发回写
window.addEventListener('storage', (e) => {
  if (e.key === null) {
    persistTokens()
  }
})

export function getAccessToken(): string {
  // 读取时自愈：localStorage 被外清而事件未及时处理时补写
  if (accessTokenCache && !localStorage.getItem(ACCESS_TOKEN_KEY)) {
    persistTokens()
  }
  return accessTokenCache
}

export function setAccessToken(token: string): void {
  accessTokenCache = token
  persistTokens()
}

export function getRefreshToken(): string {
  if (refreshTokenCache && !localStorage.getItem(REFRESH_TOKEN_KEY)) {
    persistTokens()
  }
  return refreshTokenCache
}

export function setRefreshToken(token: string): void {
  refreshTokenCache = token
  persistTokens()
}

export function clearTokens(): void {
  accessTokenCache = ''
  refreshTokenCache = ''
  persistTokens()
}
